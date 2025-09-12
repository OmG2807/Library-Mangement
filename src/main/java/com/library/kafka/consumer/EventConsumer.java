package com.library.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.kafka.event.AuditEvent;
import com.library.kafka.event.AuthorEvent;
import com.library.kafka.event.BookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    
    private final ObjectMapper objectMapper;
    
    @KafkaListener(topics = "${library.kafka.topics.book-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBookEvent(@Payload String message,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                @Header(KafkaHeaders.OFFSET) long offset,
                                Acknowledgment acknowledgment) {
        try {
            BookEvent bookEvent = objectMapper.readValue(message, BookEvent.class);
            
            log.info("Received book event: {} from topic: {}, partition: {}, offset: {}", 
                    bookEvent.getEventId(), topic, partition, offset);
            
            // Process the book event
            processBookEvent(bookEvent);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing book event from topic: {}, partition: {}, offset: {}", 
                     topic, partition, offset, e);
            // In a real application, you might want to send to a dead letter queue
        }
    }
    
    @KafkaListener(topics = "${library.kafka.topics.author-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAuthorEvent(@Payload String message,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                  @Header(KafkaHeaders.OFFSET) long offset,
                                  Acknowledgment acknowledgment) {
        try {
            AuthorEvent authorEvent = objectMapper.readValue(message, AuthorEvent.class);
            
            log.info("Received author event: {} from topic: {}, partition: {}, offset: {}", 
                    authorEvent.getEventId(), topic, partition, offset);
            
            // Process the author event
            processAuthorEvent(authorEvent);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing author event from topic: {}, partition: {}, offset: {}", 
                     topic, partition, offset, e);
        }
    }
    
    @KafkaListener(topics = "${library.kafka.topics.audit-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAuditEvent(@Payload String message,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                 @Header(KafkaHeaders.OFFSET) long offset,
                                 Acknowledgment acknowledgment) {
        try {
            AuditEvent auditEvent = objectMapper.readValue(message, AuditEvent.class);
            
            log.info("Received audit event: {} from topic: {}, partition: {}, offset: {}", 
                    auditEvent.getEventId(), topic, partition, offset);
            
            // Process the audit event
            processAuditEvent(auditEvent);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing audit event from topic: {}, partition: {}, offset: {}", 
                     topic, partition, offset, e);
        }
    }
    
    private void processBookEvent(BookEvent bookEvent) {
        log.info("Processing book event: {} - Operation: {} on Book: {} (ID: {})", 
                bookEvent.getEventType(), bookEvent.getOperation(), 
                bookEvent.getTitle(), bookEvent.getBookId());
        
        // Here you can implement business logic for book events
        // For example:
        // - Update search indexes
        // - Send notifications
        // - Update analytics
        // - Cache invalidation
        // - Integration with external systems
        
        switch (bookEvent.getOperation()) {
            case "CREATE":
                log.info("New book created: {}", bookEvent.getTitle());
                // Implement new book processing logic
                break;
            case "UPDATE":
                log.info("Book updated: {}", bookEvent.getTitle());
                // Implement book update processing logic
                break;
            case "DELETE":
                log.info("Book deleted: {}", bookEvent.getTitle());
                // Implement book deletion processing logic
                break;
            case "BORROW":
                log.info("Book borrowed: {}", bookEvent.getTitle());
                // Implement book borrowing processing logic
                break;
            case "RETURN":
                log.info("Book returned: {}", bookEvent.getTitle());
                // Implement book return processing logic
                break;
            default:
                log.warn("Unknown book operation: {}", bookEvent.getOperation());
        }
    }
    
    private void processAuthorEvent(AuthorEvent authorEvent) {
        log.info("Processing author event: {} - Operation: {} on Author: {} (ID: {})", 
                authorEvent.getEventType(), authorEvent.getOperation(), 
                authorEvent.getName(), authorEvent.getAuthorId());
        
        // Here you can implement business logic for author events
        switch (authorEvent.getOperation()) {
            case "CREATE":
                log.info("New author created: {}", authorEvent.getName());
                // Implement new author processing logic
                break;
            case "UPDATE":
                log.info("Author updated: {}", authorEvent.getName());
                // Implement author update processing logic
                break;
            case "DELETE":
                log.info("Author deleted: {}", authorEvent.getName());
                // Implement author deletion processing logic
                break;
            default:
                log.warn("Unknown author operation: {}", authorEvent.getOperation());
        }
    }
    
    private void processAuditEvent(AuditEvent auditEvent) {
        log.info("Processing audit event: {} - Action: {} on {} (ID: {}) by User: {}", 
                auditEvent.getEventType(), auditEvent.getAction(), 
                auditEvent.getResourceType(), auditEvent.getResourceId(), 
                auditEvent.getUserId());
        
        // Here you can implement audit processing logic
        // For example:
        // - Store in audit database
        // - Send to compliance systems
        // - Generate reports
        // - Alert on suspicious activities
        
        log.info("Audit details: {} - IP: {} - UserAgent: {}", 
                auditEvent.getDetails(), auditEvent.getIpAddress(), auditEvent.getUserAgent());
    }
}
