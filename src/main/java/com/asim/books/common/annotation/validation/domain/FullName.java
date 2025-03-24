package com.asim.books.common.annotation.validation.domain;

import com.asim.books.common.annotation.validation.patterns.SecureSqlString;
import com.asim.books.common.annotation.validation.patterns.TrimmedString;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

/**
 * Common validation for full name.
 * Allows alphabetic characters, spaces, hyphens, apostrophes, and periods.
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
@TrimmedString
@SecureSqlString
@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
@Pattern(regexp = ".*[a-zA-Z].*", message = "Name must contain at least one letter")
@Pattern(regexp = "^[a-zA-Z\\s'.\\-]*$", message = "Name can only contain alphabetic characters, spaces, apostrophes, periods, and hyphens")
public @interface FullName {
    Class<?>[] groups() default {};

    String message() default "Name must be between 2 and 100 characters and can only contain alphabetic characters, spaces, apostrophes, periods, and hyphens";

    Class<? extends Payload>[] payload() default {};
}