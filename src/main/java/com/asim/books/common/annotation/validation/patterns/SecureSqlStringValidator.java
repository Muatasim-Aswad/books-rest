package com.asim.books.common.annotation.validation.patterns;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SecureSqlStringValidator implements ConstraintValidator<SecureSqlString, String> {

    // Regex to detect common SQL injection patterns rather than just special characters
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i)(" +
                    // SQL comments
                    "--.*|/\\*.*\\*/|" +
                    // Common SQL keywords in suspicious contexts
                    "\\b(union\\s+select|insert\\s+into|update\\s+set|delete\\s+from|drop\\s+table|alter\\s+table|" +
                    "exec\\s+\\w+|execute\\s+\\w+|xp_\\w+|sp_\\w+|waitfor\\s+delay|cast\\s*\\(|select\\s+.*\\s+from)\\b|" +
                    // Typical SQL injection with quotes
                    "['\"][\\s;]*(?i)(or|and|union|select|insert|update|delete|drop|alter|exec|execute|waitfor|cast)\\b|" +
                    // Multiple consecutive single quotes (potential SQL escaping)
                    "'{2,}|" +
                    // Common SQL injection equality patterns
                    "\\b(\\d+)\\s*=\\s*\\1\\s+(--|or)|\\b(\\w+)\\s*=\\s*\\3\\s+(--|or)|" +
                    // Hex encoding
                    "0x[0-9a-fA-F]{16,})"
    );

    @Override
    public void initialize(SecureSqlString constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values are considered valid
        }

        return !SQL_INJECTION_PATTERN.matcher(value).find();
    }
}