package com.library.controller;

import com.library.model.Book;
import com.library.model.dto.BookRequest;
import com.library.model.dto.BookResponse;
import com.library.model.dto.PageResponse;
import com.library.service.BookService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Book operations
 */
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {
    
    private final BookService bookService;
    
    /**
     * Create a new book
     */
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest bookRequest) {
        log.info("Creating new book with title: {}", bookRequest.getTitle());
        BookResponse bookResponse = bookService.createBook(bookRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponse);
    }
    
    /**
     * Get book by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        log.info("Fetching book by ID: {}", id);
        BookResponse bookResponse = bookService.getBookById(id);
        return ResponseEntity.ok(bookResponse);
    }
    
    /**
     * Get book by ISBN
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookResponse> getBookByIsbn(@PathVariable String isbn) {
        log.info("Fetching book by ISBN: {}", isbn);
        BookResponse bookResponse = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(bookResponse);
    }
    
    /**
     * Update book by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, 
                                                  @Valid @RequestBody BookRequest bookRequest) {
        log.info("Updating book with ID: {}", id);
        BookResponse bookResponse = bookService.updateBook(id, bookRequest);
        return ResponseEntity.ok(bookResponse);
    }
    
    /**
     * Delete book by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("Deleting book with ID: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all books with pagination and sorting
     */
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        log.info("Fetching all books - page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                page, size, sortBy, sortDirection);
        
        PageResponse<BookResponse> bookPage = bookService.getAllBooks(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(bookPage);
    }
    
    /**
     * Search books with multiple criteria
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<BookResponse>> searchBooks(
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) Book.AvailabilityStatus availabilityStatus,
            @RequestParam(required = false) Integer publishedYear,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        log.info("Searching books with criteria - searchText: {}, availabilityStatus: {}, publishedYear: {}, authorId: {}", 
                searchText, availabilityStatus, publishedYear, authorId);
        
        PageResponse<BookResponse> bookPage = bookService.searchBooks(
                searchText, availabilityStatus, publishedYear, authorId, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(bookPage);
    }
    
    /**
     * Get books by availability status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<PageResponse<BookResponse>> getBooksByAvailabilityStatus(
            @PathVariable Book.AvailabilityStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Fetching books by availability status: {}", status);
        PageResponse<BookResponse> bookPage = bookService.getBooksByAvailabilityStatus(status, page, size);
        return ResponseEntity.ok(bookPage);
    }
    
    /**
     * Get books by author ID
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<PageResponse<BookResponse>> getBooksByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Fetching books by author ID: {}", authorId);
        PageResponse<BookResponse> bookPage = bookService.getBooksByAuthor(authorId, page, size);
        return ResponseEntity.ok(bookPage);
    }
    
    /**
     * Update book availability status
     */
    @PatchMapping("/{id}/availability")
    public ResponseEntity<BookResponse> updateBookAvailability(
            @PathVariable Long id,
            @RequestParam Book.AvailabilityStatus status) {
        
        log.info("Updating book availability - bookId: {}, status: {}", id, status);
        BookResponse bookResponse = bookService.updateBookAvailability(id, status);
        return ResponseEntity.ok(bookResponse);
    }
    
    /**
     * Check if book is available
     */
    @GetMapping("/{id}/available")
    public ResponseEntity<Boolean> isBookAvailable(@PathVariable Long id) {
        log.info("Checking availability for book ID: {}", id);
        boolean isAvailable = bookService.isBookAvailable(id);
        return ResponseEntity.ok(isAvailable);
    }
}