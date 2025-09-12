package com.library.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Custom validator for ISBN format
 */
public class ISBNValidator implements ConstraintValidator<ValidISBN, String> {
    
    @Override
    public void initialize(ValidISBN constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        
        // Remove hyphens and spaces
        String cleanIsbn = isbn.replaceAll("[\\s-]", "");
        
        // Check if it's a valid ISBN-10 or ISBN-13
        return isValidISBN10(cleanIsbn) || isValidISBN13(cleanIsbn);
    }
    
    private boolean isValidISBN10(String isbn) {
        if (isbn.length() != 10) {
            return false;
        }
        
        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                int digit = Character.getNumericValue(isbn.charAt(i));
                sum += digit * (10 - i);
            }
            
            char lastChar = isbn.charAt(9);
            int checkDigit = (lastChar == 'X' || lastChar == 'x') ? 10 : Character.getNumericValue(lastChar);
            sum += checkDigit;
            
            return sum % 11 == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isValidISBN13(String isbn) {
        if (isbn.length() != 13) {
            return false;
        }
        
        try {
            int sum = 0;
            for (int i = 0; i < 12; i++) {
                int digit = Character.getNumericValue(isbn.charAt(i));
                sum += (i % 2 == 0) ? digit : digit * 3;
            }
            
            int checkDigit = Character.getNumericValue(isbn.charAt(12));
            return (10 - (sum % 10)) % 10 == checkDigit;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
