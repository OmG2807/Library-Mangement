package com.library.kafka.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuditEvent extends BaseEvent {
    
    private String userId;
    private String action;
    private String resourceType; // BOOK, AUTHOR
    private Long resourceId;
    private String details;
    private String ipAddress;
    private String userAgent;
    
    public AuditEvent() {
        super();
    }
    
    public AuditEvent(String eventType, String source, String userId, String action, 
                     String resourceType, Long resourceId, String details) {
        super(eventType, source);
        this.userId = userId;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.details = details;
    }
    
    public AuditEvent(String eventType, String source, String userId, String action, 
                     String resourceType, Long resourceId, String details, 
                     String ipAddress, String userAgent) {
        this(eventType, source, userId, action, resourceType, resourceId, details);
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
}
