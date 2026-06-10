package com.myuniversity.app.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Documented
public @interface ValidPassword {

    String message() default "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
