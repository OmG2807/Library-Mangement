package com.library.kafka.event;

import com.library.model.Book;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookEvent extends BaseEvent {
    
    private Long bookId;
    private String isbn;
    private String title;
    private String operation; // CREATE, UPDATE, DELETE, BORROW, RETURN
    private Book bookData;
    
    public BookEvent() {
        super();
    }
    
    public BookEvent(String eventType, String source, Long bookId, String isbn, String title, String operation) {
        super(eventType, source);
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.operation = operation;
    }
    
    public BookEvent(String eventType, String source, Book book, String operation) {
        super(eventType, source);
        this.bookId = book.getId();
        this.isbn = book.getIsbn();
        this.title = book.getTitle();
        this.operation = operation;
        this.bookData = book;
    }
}
