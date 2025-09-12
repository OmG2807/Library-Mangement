package com.library.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

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
    
    @Min(value = 1000, message = "Birth year must be reasonable")
    @Max(value = 2024, message = "Birth year cannot be in the future")
    private Integer birthYear;
    
    private String nationality;
}
