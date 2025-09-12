package com.library.repository;

import com.library.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of custom book repository with complex queries
 */
@Repository
public class CustomBookRepositoryImpl implements CustomBookRepository {
    
    @Autowired
    private EntityManager entityManager;
    
    @Override
    public Page<Book> findBooksWithAdvancedSearch(String searchText, 
                                                Book.AvailabilityStatus availabilityStatus,
                                                Integer publishedYear,
                                                Long authorId,
                                                String sortBy,
                                                String sortDirection,
                                                Pageable pageable) {
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);
        Join<Book, com.library.model.Author> author = book.join("author", JoinType.LEFT);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // Add search criteria
        if (searchText != null && !searchText.trim().isEmpty()) {
            Predicate titlePredicate = cb.like(cb.lower(book.get("title")), 
                "%" + searchText.toLowerCase() + "%");
            Predicate authorPredicate = cb.like(cb.lower(author.get("name")), 
                "%" + searchText.toLowerCase() + "%");
            predicates.add(cb.or(titlePredicate, authorPredicate));
        }
        
        if (availabilityStatus != null) {
            predicates.add(cb.equal(book.get("availabilityStatus"), availabilityStatus));
        }
        
        if (publishedYear != null) {
            predicates.add(cb.equal(book.get("publishedYear"), publishedYear));
        }
        
        if (authorId != null) {
            predicates.add(cb.equal(author.get("id"), authorId));
        }
        
        query.where(predicates.toArray(new Predicate[0]));
        
        // Add sorting
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            Path<?> sortPath = getSortPath(book, author, sortBy);
            if (sortPath != null) {
                if ("desc".equalsIgnoreCase(sortDirection)) {
                    query.orderBy(cb.desc(sortPath));
                } else {
                    query.orderBy(cb.asc(sortPath));
                }
            }
        } else {
            query.orderBy(cb.desc(book.get("createdAt")));
        }
        
        // Execute query with pagination
        TypedQuery<Book> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        
        List<Book> books = typedQuery.getResultList();
        
        // Count total results
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Book> countBook = countQuery.from(Book.class);
        Join<Book, com.library.model.Author> countAuthor = countBook.join("author", JoinType.LEFT);
        
        countQuery.select(cb.count(countBook));
        countQuery.where(predicates.toArray(new Predicate[0]));
        
        Long total = entityManager.createQuery(countQuery).getSingleResult();
        
        return new PageImpl<>(books, pageable, total);
    }
    
    @Override
    public List<Book> findPopularBooks(int limit) {
        // For now, return recently added books as popular books
        // In a real system, this would be based on transaction/borrowing data
        return findRecentlyAddedBooks(limit);
    }
    
    @Override
    public List<Book> findRecentlyAddedBooks(int limit) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);
        
        query.orderBy(cb.desc(book.get("createdAt")));
        
        TypedQuery<Book> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(limit);
        
        return typedQuery.getResultList();
    }
    
    @Override
    public List<Book> findBooksByAuthorWithCount(Long authorId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);
        Join<Book, com.library.model.Author> author = book.join("author");
        
        query.where(cb.equal(author.get("id"), authorId));
        query.orderBy(cb.asc(book.get("title")));
        
        return entityManager.createQuery(query).getResultList();
    }
    
    @Override
    public List<Book> findSimilarBooks(Long bookId, int limit) {
        // Find the book first to get its author
        Book targetBook = entityManager.find(Book.class, bookId);
        if (targetBook == null) {
            return new ArrayList<>();
        }
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);
        Join<Book, com.library.model.Author> author = book.join("author");
        
        // Find books by the same author, excluding the current book
        query.where(cb.and(
            cb.equal(author.get("id"), targetBook.getAuthor().getId()),
            cb.notEqual(book.get("id"), bookId)
        ));
        
        query.orderBy(cb.desc(book.get("createdAt")));
        
        TypedQuery<Book> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(limit);
        
        return typedQuery.getResultList();
    }
    
    @Override
    public BookStatistics getBookStatistics() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        // Count total books
        CriteriaQuery<Long> totalQuery = cb.createQuery(Long.class);
        totalQuery.select(cb.count(totalQuery.from(Book.class)));
        long totalBooks = entityManager.createQuery(totalQuery).getSingleResult();
        
        // Count books by availability status
        long availableBooks = countBooksByStatus(Book.AvailabilityStatus.AVAILABLE);
        long borrowedBooks = countBooksByStatus(Book.AvailabilityStatus.BORROWED);
        long maintenanceBooks = countBooksByStatus(Book.AvailabilityStatus.MAINTENANCE);
        long lostBooks = countBooksByStatus(Book.AvailabilityStatus.LOST);
        
        // Count total authors
        CriteriaQuery<Long> authorQuery = cb.createQuery(Long.class);
        authorQuery.select(cb.count(authorQuery.from(com.library.model.Author.class)));
        long totalAuthors = entityManager.createQuery(authorQuery).getSingleResult();
        
        return new BookStatistics(totalBooks, availableBooks, borrowedBooks, 
                                maintenanceBooks, lostBooks, totalAuthors);
    }
    
    private long countBooksByStatus(Book.AvailabilityStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Book> book = query.from(Book.class);
        
        query.select(cb.count(book));
        query.where(cb.equal(book.get("availabilityStatus"), status));
        
        return entityManager.createQuery(query).getSingleResult();
    }
    
    private Path<?> getSortPath(Root<Book> book, Join<Book, com.library.model.Author> author, String sortBy) {
        switch (sortBy.toLowerCase()) {
            case "title":
                return book.get("title");
            case "publishedyear":
            case "published_year":
                return book.get("publishedYear");
            case "availabilitystatus":
            case "availability_status":
                return book.get("availabilityStatus");
            case "createdat":
            case "created_at":
                return book.get("createdAt");
            case "authorname":
            case "author_name":
                return author.get("name");
            default:
                return book.get("createdAt");
        }
    }
}