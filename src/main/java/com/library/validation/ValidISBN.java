package com.library.validation;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for ISBN
 */
@Documented
@Constraint(validatedBy = ISBNValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidISBN {
    
    String message() default "Invalid ISBN format";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
