package com.library.service;

import com.library.model.Book;
import com.library.model.dto.BookRequest;
import com.library.model.dto.BookResponse;
import com.library.model.dto.PageResponse;
import com.library.repository.CustomBookRepository;

import java.util.List;

/**
 * Service interface for Book operations
 */
public interface BookService {
    
    /**
     * Create a new book
     */
    BookResponse createBook(BookRequest bookRequest);
    
    /**
     * Get book by ID
     */
    BookResponse getBookById(Long id);
    
    /**
     * Get book by ISBN
     */
    BookResponse getBookByIsbn(String isbn);
    
    /**
     * Update book by ID
     */
    BookResponse updateBook(Long id, BookRequest bookRequest);
    
    /**
     * Delete book by ID
     */
    void deleteBook(Long id);
    
    /**
     * Get all books with pagination and filters
     */
    PageResponse<BookResponse> getAllBooks(int page, int size, String sortBy, String sortDirection);
    
    /**
     * Search books with multiple criteria
     */
    PageResponse<BookResponse> searchBooks(String searchText, Book.AvailabilityStatus availabilityStatus,
                                         Integer publishedYear, Long authorId,
                                         int page, int size, String sortBy, String sortDirection);
    
    /**
     * Get books by availability status
     */
    PageResponse<BookResponse> getBooksByAvailabilityStatus(Book.AvailabilityStatus status, int page, int size);
    
    /**
     * Get books by author
     */
    PageResponse<BookResponse> getBooksByAuthor(Long authorId, int page, int size);
    
    
    /**
     * Get popular books
     */
    List<BookResponse> getPopularBooks(int limit);
    
    /**
     * Get recently added books
     */
    List<BookResponse> getRecentlyAddedBooks(int limit);
    
    /**
     * Get similar books
     */
    List<BookResponse> getSimilarBooks(Long bookId, int limit);
    
    /**
     * Get book statistics
     */
    CustomBookRepository.BookStatistics getBookStatistics();
    
    /**
     * Update book availability status
     */
    BookResponse updateBookAvailability(Long bookId, Book.AvailabilityStatus status);
    
    /**
     * Check if book is available for borrowing
     */
    boolean isBookAvailable(Long bookId);
}
