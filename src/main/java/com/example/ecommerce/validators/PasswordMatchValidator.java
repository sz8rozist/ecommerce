package com.example.ecommerce.validators;

import com.example.ecommerce.request.ResetPasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, ResetPasswordRequest> {

    @Override
    public boolean isValid(ResetPasswordRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // más validátor fogja elkapni, pl. @NotNull
        }

        if (request.getNewPassword() == null || request.getNewPasswordConfirm() == null) {
            return false;
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("A két jelszó nem egyezik!")
                    .addPropertyNode("newPasswordConfirm")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
