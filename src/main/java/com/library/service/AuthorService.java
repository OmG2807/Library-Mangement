package com.library.service;

import com.library.model.Author;
import com.library.model.dto.AuthorRequest;
import com.library.model.dto.PageResponse;

/**
 * Service interface for Author operations
 */
public interface AuthorService {
    
    /**
     * Create a new author
     */
    Author createAuthor(AuthorRequest authorRequest);
    
    /**
     * Get author by ID
     */
    Author getAuthorById(Long id);
    
    /**
     * Get author by name
     */
    Author getAuthorByName(String name);
    
    /**
     * Update author by ID
     */
    Author updateAuthor(Long id, AuthorRequest authorRequest);
    
    /**
     * Delete author by ID
     */
    void deleteAuthor(Long id);
    
    /**
     * Get all authors with pagination
     */
    PageResponse<Author> getAllAuthors(int page, int size, String sortBy, String sortDirection);
    
    /**
     * Search authors by name
     */
    PageResponse<Author> searchAuthors(String searchText, int page, int size);
    
    /**
     * Get authors by birth year
     */
    PageResponse<Author> getAuthorsByBirthYear(Integer birthYear, int page, int size);
    
    /**
     * Get authors by birth year range
     */
    PageResponse<Author> getAuthorsByBirthYearRange(Integer startYear, Integer endYear, int page, int size);
    
    /**
     * Get authors with biography
     */
    PageResponse<Author> getAuthorsWithBiography(int page, int size);
    
    /**
     * Get authors without biography
     */
    PageResponse<Author> getAuthorsWithoutBiography(int page, int size);
    
    /**
     * Check if author exists by name
     */
    boolean authorExistsByName(String name);
}
