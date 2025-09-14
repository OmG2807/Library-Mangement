package com.library.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * DTO for author creation and update requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequest {
    
    @NotBlank(message = "Author name is required")
    private String name;
    
    private String biography;
    
    private String nationality;
}
