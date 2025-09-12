package com.library.service;

import com.library.kafka.event.AuditEvent;
import com.library.kafka.event.AuthorEvent;
import com.library.kafka.event.BookEvent;
import com.library.kafka.producer.EventProducer;
import com.library.model.Author;
import com.library.model.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    
    private final EventProducer eventProducer;
    
    public void publishBookEvent(Book book, String operation) {
        try {
            BookEvent bookEvent = new BookEvent(
                "BOOK_EVENT", 
                "library-management-system", 
                book, 
                operation
            );
            eventProducer.publishBookEvent(bookEvent);
            log.info("Published book event for operation: {} on book: {}", operation, book.getTitle());
        } catch (Exception e) {
            log.error("Failed to publish book event for operation: {} on book: {}", operation, book.getTitle(), e);
        }
    }
    
    public void publishAuthorEvent(Author author, String operation) {
        try {
            AuthorEvent authorEvent = new AuthorEvent(
                "AUTHOR_EVENT", 
                "library-management-system", 
                author, 
                operation
            );
            eventProducer.publishAuthorEvent(authorEvent);
            log.info("Published author event for operation: {} on author: {}", operation, author.getName());
        } catch (Exception e) {
            log.error("Failed to publish author event for operation: {} on author: {}", operation, author.getName(), e);
        }
    }
    
    public void publishAuditEvent(String userId, String action, String resourceType, Long resourceId, String details) {
        try {
            AuditEvent auditEvent = new AuditEvent(
                "AUDIT_EVENT", 
                "library-management-system", 
                userId, 
                action, 
                resourceType, 
                resourceId, 
                details
            );
            eventProducer.publishAuditEvent(auditEvent);
            log.info("Published audit event for action: {} on {}: {}", action, resourceType, resourceId);
        } catch (Exception e) {
            log.error("Failed to publish audit event for action: {} on {}: {}", action, resourceType, resourceId, e);
        }
    }
    
    public void publishAuditEvent(String userId, String action, String resourceType, Long resourceId, 
                                 String details, String ipAddress, String userAgent) {
        try {
            AuditEvent auditEvent = new AuditEvent(
                "AUDIT_EVENT", 
                "library-management-system", 
                userId, 
                action, 
                resourceType, 
                resourceId, 
                details,
                ipAddress,
                userAgent
            );
            eventProducer.publishAuditEvent(auditEvent);
            log.info("Published audit event for action: {} on {}: {}", action, resourceType, resourceId);
        } catch (Exception e) {
            log.error("Failed to publish audit event for action: {} on {}: {}", action, resourceType, resourceId, e);
        }
    }
}
