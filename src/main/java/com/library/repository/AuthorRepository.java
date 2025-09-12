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
     * Find authors by birth year
     */
    Page<Author> findByBirthYear(Integer birthYear, Pageable pageable);
    
    /**
     * Find authors by birth year range
     */
    Page<Author> findByBirthYearBetween(Integer startYear, Integer endYear, Pageable pageable);
    
    /**
     * Find authors with biography
     */
    @Query("SELECT a FROM Author a WHERE a.biography IS NOT NULL AND a.biography != ''")
    Page<Author> findAuthorsWithBiography(Pageable pageable);
    
    /**
     * Find authors without biography
     */
    @Query("SELECT a FROM Author a WHERE a.biography IS NULL OR a.biography = ''")
    Page<Author> findAuthorsWithoutBiography(Pageable pageable);
    
    /**
     * Find authors by multiple names
     */
    @Query("SELECT a FROM Author a WHERE a.name IN :names")
    List<Author> findByNameIn(@Param("names") List<String> names);
    
    /**
     * Find authors with books count (using projection)
     */
    @Query("SELECT a FROM Author a")
    Page<Author> findAllWithProjection(Pageable pageable);
    
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
