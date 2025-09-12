package com.library.model.dto;

import com.library.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for book response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    
    private Long id;
    private String title;
    private String isbn;
    private Integer publishedYear;
    private Book.AvailabilityStatus availabilityStatus;
    private AuthorInfo author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private Long authorId;
        private String name;
    }
    
}
