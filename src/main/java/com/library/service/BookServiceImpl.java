package com.library.service;

import com.library.model.Author;
import com.library.model.Book;
import com.library.model.dto.BookRequest;
import com.library.model.dto.BookResponse;
import com.library.model.dto.PageResponse;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.CustomBookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Book operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookServiceImpl implements BookService {
    
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CustomBookRepository customBookRepository;
    private final EventService eventService;
    
    @Override
    public BookResponse createBook(BookRequest bookRequest) {
        log.info("Creating new book with title: {}", bookRequest.getTitle());
        
        // Validate ISBN uniqueness
        if (bookRepository.existsByIsbn(bookRequest.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN " + bookRequest.getIsbn() + " already exists");
        }
        
        // Validate author exists
        Author author = authorRepository.findById(bookRequest.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + bookRequest.getAuthorId()));
        
        // Create book entity
        Book book = new Book(
                bookRequest.getTitle(),
                bookRequest.getIsbn(),
                bookRequest.getPublishedYear(),
                Book.AvailabilityStatus.AVAILABLE,
                author
        );
        
        Book savedBook = bookRepository.save(book);
        log.info("Book created successfully with ID: {}", savedBook.getId());
        
        // Publish book creation event
        eventService.publishBookEvent(savedBook, "CREATE");
        eventService.publishAuditEvent("system", "CREATE_BOOK", "BOOK", savedBook.getId(), 
                "Book created: " + savedBook.getTitle());
        
        return convertToBookResponse(savedBook);
    }
    
    @Override
    public BookResponse getBookById(Long id) {
        log.info("Fetching book by ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + id));
        return convertToBookResponse(book);
    }
    
    @Override
    public BookResponse getBookByIsbn(String isbn) {
        log.info("Fetching book by ISBN: {}", isbn);
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ISBN: " + isbn));
        return convertToBookResponse(book);
    }
    
    @Override
    public BookResponse updateBook(Long id, BookRequest bookRequest) {
        log.info("Updating book with ID: {}", id);
        
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + id));
        
        // Validate ISBN uniqueness if changed
        if (!existingBook.getIsbn().equals(bookRequest.getIsbn()) && 
            bookRepository.existsByIsbn(bookRequest.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN " + bookRequest.getIsbn() + " already exists");
        }
        
        // Validate author exists
        Author author = authorRepository.findById(bookRequest.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + bookRequest.getAuthorId()));
        
        // Update book fields
        existingBook.setTitle(bookRequest.getTitle());
        existingBook.setIsbn(bookRequest.getIsbn());
        existingBook.setPublishedYear(bookRequest.getPublishedYear());
        existingBook.setAuthor(author);
        existingBook.setUpdatedAt(LocalDateTime.now());
        
        Book updatedBook = bookRepository.save(existingBook);
        log.info("Book updated successfully with ID: {}", updatedBook.getId());
        
        // Publish book update event
        eventService.publishBookEvent(updatedBook, "UPDATE");
        eventService.publishAuditEvent("system", "UPDATE_BOOK", "BOOK", updatedBook.getId(), 
                "Book updated: " + updatedBook.getTitle());
        
        return convertToBookResponse(updatedBook);
    }
    
    @Override
    public void deleteBook(Long id) {
        log.info("Deleting book with ID: {}", id);
        
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + id));
        
        // Publish book deletion event before deleting
        eventService.publishBookEvent(book, "DELETE");
        eventService.publishAuditEvent("system", "DELETE_BOOK", "BOOK", book.getId(), 
                "Book deleted: " + book.getTitle());
        
        bookRepository.deleteById(id);
        log.info("Book deleted successfully with ID: {}", id);
    }
    
    @Override
    public PageResponse<BookResponse> getAllBooks(int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching all books - page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                page, size, sortBy, sortDirection);
        
        Sort sort = createSort(sortBy, sortDirection);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> bookPage = bookRepository.findAll(pageable);
        
        List<BookResponse> bookResponses = bookPage.getContent().stream()
                .map(this::convertToBookResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(bookResponses, bookPage.getNumber(), 
                bookPage.getSize(), bookPage.getTotalElements());
    }
    
    @Override
    public PageResponse<BookResponse> searchBooks(String searchText, Book.AvailabilityStatus availabilityStatus,
                                                Integer publishedYear, Long authorId,
                                                int page, int size, String sortBy, String sortDirection) {
        log.info("Searching books - searchText: {}, availabilityStatus: {}, publishedYear: {}, authorId: {}, page: {}, size: {}", 
                searchText, availabilityStatus, publishedYear, authorId, page, size);
        
        Sort sort = createSort(sortBy, sortDirection);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Book> bookPage = bookRepository.findBySearchCriteria(
                searchText, availabilityStatus, publishedYear, authorId, pageable);
        
        List<BookResponse> bookResponses = bookPage.getContent().stream()
                .map(this::convertToBookResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(bookResponses, bookPage.getNumber(), 
                bookPage.getSize(), bookPage.getTotalElements());
    }
    
    @Override
    public PageResponse<BookResponse> getBooksByAvailabilityStatus(Book.AvailabilityStatus status, int page, int size) {
        log.info("Fetching books by availability status: {} - page: {}, size: {}", status, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findByAvailabilityStatus(status, pageable);
        
        List<BookResponse> bookResponses = bookPage.getContent().stream()
                .map(this::convertToBookResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(bookResponses, bookPage.getNumber(), 
                bookPage.getSize(), bookPage.getTotalElements());
    }
    
    @Override
    public PageResponse<BookResponse> getBooksByAuthor(Long authorId, int page, int size) {
        log.info("Fetching books by author ID: {} - page: {}, size: {}", authorId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findByAuthorId(authorId, pageable);
        
        List<BookResponse> bookResponses = bookPage.getContent().stream()
                .map(this::convertToBookResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(bookResponses, bookPage.getNumber(), 
                bookPage.getSize(), bookPage.getTotalElements());
    }
    
    @Override
    public List<BookResponse> getPopularBooks(int limit) {
        log.info("Fetching popular books with limit: {}", limit);
        
        List<Book> books = customBookRepository.findPopularBooks(limit);
        return books.stream()
                .map(this::convertToBookResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BookResponse> getRecentlyAddedBooks(int limit) {
        log.info("Fetching recently added books with limit: {}", limit);
        
        List<Book> books = customBookRepository.findRecentlyAddedBooks(limit);
        return books.stream()
                .map(this::convertToBookResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<BookResponse> getSimilarBooks(Long bookId, int limit) {
        log.info("Fetching similar books for book ID: {} with limit: {}", bookId, limit);
        
        List<Book> books = customBookRepository.findSimilarBooks(bookId, limit);
        return books.stream()
                .map(this::convertToBookResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public CustomBookRepository.BookStatistics getBookStatistics() {
        log.info("Fetching book statistics");
        return customBookRepository.getBookStatistics();
    }
    
    @Override
    public BookResponse updateBookAvailability(Long bookId, Book.AvailabilityStatus status) {
        log.info("Updating book availability - bookId: {}, status: {}", bookId, status);
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));
        
        book.setAvailabilityStatus(status);
        book.setUpdatedAt(LocalDateTime.now());
        
        Book updatedBook = bookRepository.save(book);
        log.info("Book availability updated successfully - bookId: {}, status: {}", bookId, status);
        
        // Publish book availability update event
        String operation = status == Book.AvailabilityStatus.AVAILABLE ? "RETURN" : "BORROW";
        eventService.publishBookEvent(updatedBook, operation);
        eventService.publishAuditEvent("system", "UPDATE_BOOK_AVAILABILITY", "BOOK", updatedBook.getId(), 
                "Book availability changed to: " + status + " for book: " + updatedBook.getTitle());
        
        return convertToBookResponse(updatedBook);
    }
    
    @Override
    public boolean isBookAvailable(Long bookId) {
        log.info("Checking book availability for ID: {}", bookId);
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));
        
        return book.getAvailabilityStatus() == Book.AvailabilityStatus.AVAILABLE;
    }
    
    private BookResponse convertToBookResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setIsbn(book.getIsbn());
        response.setPublishedYear(book.getPublishedYear());
        response.setAvailabilityStatus(book.getAvailabilityStatus());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());
        
        // Set author information
        if (book.getAuthor() != null) {
            BookResponse.AuthorInfo authorInfo = new BookResponse.AuthorInfo();
            authorInfo.setAuthorId(book.getAuthor().getId());
            authorInfo.setName(book.getAuthor().getName());
            response.setAuthor(authorInfo);
        }
        
        return response;
    }
    
    private Sort createSort(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "createdAt";
        }
        
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        return Sort.by(direction, sortBy);
    }
}