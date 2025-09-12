package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Book entity with custom query methods
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    /**
     * Find book by ISBN
     */
    Optional<Book> findByIsbn(String isbn);
    
    /**
     * Check if book exists by ISBN
     */
    boolean existsByIsbn(String isbn);
    
    /**
     * Find books by availability status with pagination
     */
    Page<Book> findByAvailabilityStatus(Book.AvailabilityStatus availabilityStatus, Pageable pageable);
    
    /**
     * Find books by author ID with pagination
     */
    Page<Book> findByAuthorId(Long authorId, Pageable pageable);
    
    
    /**
     * Find books by published year with pagination
     */
    Page<Book> findByPublishedYear(Integer publishedYear, Pageable pageable);
    
    /**
     * Find books by published year range with pagination
     */
    Page<Book> findByPublishedYearBetween(Integer startYear, Integer endYear, Pageable pageable);
    
    /**
     * Find books by author name (partial match, case insensitive)
     */
    @Query("SELECT b FROM Book b JOIN b.author a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    Page<Book> findByAuthorNameContainingIgnoreCase(@Param("authorName") String authorName, Pageable pageable);
    
    /**
     * Find books by title (partial match, case insensitive)
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Book> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);
    
    /**
     * Full-text search across title and author name
     */
    @Query("SELECT b FROM Book b JOIN b.author a WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<Book> findByTextSearch(@Param("searchText") String searchText, Pageable pageable);
    
    /**
     * Complex search with multiple filters
     */
    @Query("SELECT b FROM Book b JOIN b.author a WHERE " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchText, '%'))) AND " +
           "(:availabilityStatus IS NULL OR b.availabilityStatus = :availabilityStatus) AND " +
           "(:publishedYear IS NULL OR b.publishedYear = :publishedYear) AND " +
           "(:authorId IS NULL OR a.id = :authorId)")
    Page<Book> findBySearchCriteria(@Param("searchText") String searchText, 
                                   @Param("availabilityStatus") Book.AvailabilityStatus availabilityStatus, 
                                   @Param("publishedYear") Integer publishedYear, 
                                   @Param("authorId") Long authorId, Pageable pageable);
    
    /**
     * Find books by multiple availability statuses
     */
    @Query("SELECT b FROM Book b WHERE b.availabilityStatus IN :statuses")
    Page<Book> findByAvailabilityStatusIn(@Param("statuses") List<Book.AvailabilityStatus> statuses, Pageable pageable);
    
    /**
     * Find recently added books
     */
    @Query("SELECT b FROM Book b WHERE b.createdAt >= :since")
    Page<Book> findRecentlyAdded(@Param("since") java.time.LocalDateTime since, Pageable pageable);
    
    /**
     * Count books by availability status
     */
    long countByAvailabilityStatus(Book.AvailabilityStatus availabilityStatus);
    
    /**
     * Count books by author
     */
    long countByAuthorId(Long authorId);
    
    
    /**
     * Find all books with pagination (for general listing)
     */
    @Override
    Page<Book> findAll(Pageable pageable);
}
