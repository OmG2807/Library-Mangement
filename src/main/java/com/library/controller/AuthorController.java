package com.library.controller;

import com.library.model.Author;
import com.library.model.dto.AuthorRequest;
import com.library.model.dto.PageResponse;
import com.library.service.AuthorService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Author operations
 */
@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@Slf4j
public class AuthorController {
    
    private final AuthorService authorService;
    
    /**
     * Create a new author
     */
    @PostMapping
    public ResponseEntity<Author> createAuthor(@Valid @RequestBody AuthorRequest authorRequest) {
        log.info("Creating new author with name: {}", authorRequest.getName());
        Author author = authorService.createAuthor(authorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(author);
    }
    
    /**
     * Get author by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        log.info("Fetching author by ID: {}", id);
        Author author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }
    
    /**
     * Get author by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Author> getAuthorByName(@PathVariable String name) {
        log.info("Fetching author by name: {}", name);
        Author author = authorService.getAuthorByName(name);
        return ResponseEntity.ok(author);
    }
    
    /**
     * Update author by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, 
                                              @Valid @RequestBody AuthorRequest authorRequest) {
        log.info("Updating author with ID: {}", id);
        Author author = authorService.updateAuthor(id, authorRequest);
        return ResponseEntity.ok(author);
    }
    
    /**
     * Delete author by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        log.info("Deleting author with ID: {}", id);
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all authors with pagination and sorting
     */
    @GetMapping
    public ResponseEntity<PageResponse<Author>> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        log.info("Fetching all authors - page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                page, size, sortBy, sortDirection);
        
        PageResponse<Author> authorPage = authorService.getAllAuthors(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(authorPage);
    }
    
    /**
     * Search authors by name
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<Author>> searchAuthors(
            @RequestParam String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Searching authors with text: {}", searchText);
        PageResponse<Author> authorPage = authorService.searchAuthors(searchText, page, size);
        return ResponseEntity.ok(authorPage);
    }
    
}
