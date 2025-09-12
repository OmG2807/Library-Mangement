package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Custom repository interface for complex book queries
 */
public interface CustomBookRepository {
    
    /**
     * Advanced search with multiple criteria and sorting options
     */
    Page<Book> findBooksWithAdvancedSearch(String searchText, 
                                          Book.AvailabilityStatus availabilityStatus,
                                          Integer publishedYear,
                                          Long authorId,
                                          String sortBy,
                                          String sortDirection,
                                          Pageable pageable);
    
    /**
     * Find popular books based on transaction count
     */
    List<Book> findPopularBooks(int limit);
    
    /**
     * Find recently added books
     */
    List<Book> findRecentlyAddedBooks(int limit);
    
    /**
     * Find books by author with book count
     */
    List<Book> findBooksByAuthorWithCount(Long authorId);
    
    /**
     * Find similar books based on author
     */
    List<Book> findSimilarBooks(Long bookId, int limit);
    
    /**
     * Get book statistics
     */
    BookStatistics getBookStatistics();
    
    /**
     * Inner class for book statistics
     */
    class BookStatistics {
        private long totalBooks;
        private long availableBooks;
        private long borrowedBooks;
        private long maintenanceBooks;
        private long lostBooks;
        private long totalAuthors;
        
        // Constructors, getters, and setters
        public BookStatistics() {}
        
        public BookStatistics(long totalBooks, long availableBooks, long borrowedBooks, 
                            long maintenanceBooks, long lostBooks, long totalAuthors) {
            this.totalBooks = totalBooks;
            this.availableBooks = availableBooks;
            this.borrowedBooks = borrowedBooks;
            this.maintenanceBooks = maintenanceBooks;
            this.lostBooks = lostBooks;
            this.totalAuthors = totalAuthors;
        }
        
        // Getters and setters
        public long getTotalBooks() { return totalBooks; }
        public void setTotalBooks(long totalBooks) { this.totalBooks = totalBooks; }
        
        public long getAvailableBooks() { return availableBooks; }
        public void setAvailableBooks(long availableBooks) { this.availableBooks = availableBooks; }
        
        public long getBorrowedBooks() { return borrowedBooks; }
        public void setBorrowedBooks(long borrowedBooks) { this.borrowedBooks = borrowedBooks; }
        
        public long getMaintenanceBooks() { return maintenanceBooks; }
        public void setMaintenanceBooks(long maintenanceBooks) { this.maintenanceBooks = maintenanceBooks; }
        
        public long getLostBooks() { return lostBooks; }
        public void setLostBooks(long lostBooks) { this.lostBooks = lostBooks; }
        
        public long getTotalAuthors() { return totalAuthors; }
        public void setTotalAuthors(long totalAuthors) { this.totalAuthors = totalAuthors; }
        
    }
}
