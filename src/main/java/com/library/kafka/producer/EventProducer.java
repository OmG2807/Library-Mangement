package com.library.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.kafka.event.AuditEvent;
import com.library.kafka.event.AuthorEvent;
import com.library.kafka.event.BookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${library.kafka.topics.book-events}")
    private String bookEventsTopic;
    
    @Value("${library.kafka.topics.author-events}")
    private String authorEventsTopic;
    
    @Value("${library.kafka.topics.audit-events}")
    private String auditEventsTopic;
    
    public void publishBookEvent(BookEvent bookEvent) {
        try {
            String message = objectMapper.writeValueAsString(bookEvent);
            String key = bookEvent.getBookId() != null ? bookEvent.getBookId().toString() : bookEvent.getIsbn();
            
            ListenableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(bookEventsTopic, key, message);
            
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    log.info("Book event published successfully: {} with offset: {}", 
                            bookEvent.getEventId(), result.getRecordMetadata().offset());
                }
                
                @Override
                public void onFailure(Throwable ex) {
                    log.error("Failed to publish book event: {}", bookEvent.getEventId(), ex);
                }
            });
            
        } catch (JsonProcessingException e) {
            log.error("Error serializing book event: {}", bookEvent.getEventId(), e);
        }
    }
    
    public void publishAuthorEvent(AuthorEvent authorEvent) {
        try {
            String message = objectMapper.writeValueAsString(authorEvent);
            String key = authorEvent.getAuthorId() != null ? authorEvent.getAuthorId().toString() : authorEvent.getName();
            
            ListenableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(authorEventsTopic, key, message);
            
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    log.info("Author event published successfully: {} with offset: {}", 
                            authorEvent.getEventId(), result.getRecordMetadata().offset());
                }
                
                @Override
                public void onFailure(Throwable ex) {
                    log.error("Failed to publish author event: {}", authorEvent.getEventId(), ex);
                }
            });
            
        } catch (JsonProcessingException e) {
            log.error("Error serializing author event: {}", authorEvent.getEventId(), e);
        }
    }
    
    public void publishAuditEvent(AuditEvent auditEvent) {
        try {
            String message = objectMapper.writeValueAsString(auditEvent);
            String key = auditEvent.getResourceId() != null ? 
                auditEvent.getResourceId().toString() : auditEvent.getUserId();
            
            ListenableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(auditEventsTopic, key, message);
            
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    log.info("Audit event published successfully: {} with offset: {}", 
                            auditEvent.getEventId(), result.getRecordMetadata().offset());
                }
                
                @Override
                public void onFailure(Throwable ex) {
                    log.error("Failed to publish audit event: {}", auditEvent.getEventId(), ex);
                }
            });
            
        } catch (JsonProcessingException e) {
            log.error("Error serializing audit event: {}", auditEvent.getEventId(), e);
        }
    }
}
