package com.library.service;

import com.library.model.Author;
import com.library.model.dto.AuthorRequest;
import com.library.model.dto.PageResponse;
import com.library.repository.AuthorRepository;
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

/**
 * Service implementation for Author operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthorServiceImpl implements AuthorService {
    
    private final AuthorRepository authorRepository;
    
    @Override
    public Author createAuthor(AuthorRequest authorRequest) {
        log.info("Creating new author with name: {}", authorRequest.getName());
        
        // Check if author already exists
        if (authorRepository.existsByName(authorRequest.getName())) {
            throw new IllegalArgumentException("Author with name " + authorRequest.getName() + " already exists");
        }
        
        Author author = new Author(
                authorRequest.getName(),
                authorRequest.getBiography(),
                authorRequest.getNationality()
        );
        
        Author savedAuthor = authorRepository.save(author);
        log.info("Author created successfully with ID: {}", savedAuthor.getId());
        
        
        return savedAuthor;
    }
    
    @Override
    public Author getAuthorById(Long id) {
        log.info("Fetching author by ID: {}", id);
        return authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + id));
    }
    
    @Override
    public Author getAuthorByName(String name) {
        log.info("Fetching author by name: {}", name);
        return authorRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with name: " + name));
    }
    
    @Override
    public Author updateAuthor(Long id, AuthorRequest authorRequest) {
        log.info("Updating author with ID: {}", id);
        
        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + id));
        
        // Check if name is being changed and if new name already exists
        if (!existingAuthor.getName().equals(authorRequest.getName()) && 
            authorRepository.existsByName(authorRequest.getName())) {
            throw new IllegalArgumentException("Author with name " + authorRequest.getName() + " already exists");
        }
        
        existingAuthor.setName(authorRequest.getName());
        existingAuthor.setBiography(authorRequest.getBiography());
        existingAuthor.setNationality(authorRequest.getNationality());
        existingAuthor.setUpdatedAt(LocalDateTime.now());
        
        Author updatedAuthor = authorRepository.save(existingAuthor);
        log.info("Author updated successfully with ID: {}", updatedAuthor.getId());
        
        
        return updatedAuthor;
    }
    
    @Override
    public void deleteAuthor(Long id) {
        log.info("Deleting author with ID: {}", id);
        
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + id));
        
        
        authorRepository.deleteById(id);
        log.info("Author deleted successfully with ID: {}", id);
    }
    
    @Override
    public PageResponse<Author> getAllAuthors(int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching all authors - page: {}, size: {}, sortBy: {}, sortDirection: {}", 
                page, size, sortBy, sortDirection);
        
        Sort sort = createSort(sortBy, sortDirection);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Author> authorPage = authorRepository.findAll(pageable);
        
        return new PageResponse<>(authorPage.getContent(), authorPage.getNumber(), 
                authorPage.getSize(), authorPage.getTotalElements());
    }
    
    @Override
    public PageResponse<Author> searchAuthors(String searchText, int page, int size) {
        log.info("Searching authors - searchText: {}, page: {}, size: {}", searchText, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Author> authorPage = authorRepository.findByTextSearch(searchText, pageable);
        
        return new PageResponse<>(authorPage.getContent(), authorPage.getNumber(), 
                authorPage.getSize(), authorPage.getTotalElements());
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