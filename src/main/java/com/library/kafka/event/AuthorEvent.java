package com.library.kafka.event;

import com.library.model.Author;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorEvent extends BaseEvent {
    
    private Long authorId;
    private String name;
    private String nationality;
    private String operation; // CREATE, UPDATE, DELETE
    private Author authorData;
    
    public AuthorEvent() {
        super();
    }
    
    public AuthorEvent(String eventType, String source, Long authorId, String name, String nationality, String operation) {
        super(eventType, source);
        this.authorId = authorId;
        this.name = name;
        this.nationality = nationality;
        this.operation = operation;
    }
    
    public AuthorEvent(String eventType, String source, Author author, String operation) {
        super(eventType, source);
        this.authorId = author.getId();
        this.name = author.getName();
        this.nationality = author.getNationality();
        this.operation = operation;
        this.authorData = author;
    }
}
