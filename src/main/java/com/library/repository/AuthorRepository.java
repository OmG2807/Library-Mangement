package com.library.repository;

import com.library.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Author entity with custom query methods
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    /**
     * Find author by name (exact match)
     */
    Optional<Author> findByName(String name);
    
    /**
     * Find author by name (partial match, case insensitive)
     */
    @Query("SELECT a FROM Author a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Author> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    /**
     * Full-text search on author name
     */
    @Query("SELECT a FROM Author a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<Author> findByTextSearch(@Param("searchText") String searchText, Pageable pageable);
    
    /**
     * Check if author exists by name
     */
    boolean existsByName(String name);
    
    /**
     * Find all authors with pagination
     */
    @Override
    Page<Author> findAll(Pageable pageable);
}
