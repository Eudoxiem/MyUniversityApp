package com.myuniversity.app.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MIN_LENGTH = 8;
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String LOWERCASE_PATTERN = ".*[a-z].*";
    private static final String DIGIT_PATTERN = ".*\\d.*";
    private static final String SPECIAL_PATTERN = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~].*";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        boolean isValid = password.length() >= MIN_LENGTH
                && password.matches(UPPERCASE_PATTERN)
                && password.matches(LOWERCASE_PATTERN)
                && password.matches(DIGIT_PATTERN)
                && password.matches(SPECIAL_PATTERN);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial"
            ).addConstraintViolation();
        }

        return isValid;
    }
}
