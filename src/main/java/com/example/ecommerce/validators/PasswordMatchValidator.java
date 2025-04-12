package com.example.ecommerce.validators;

import com.example.ecommerce.request.ResetPasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, ResetPasswordRequest> {

    @Override
    public boolean isValid(ResetPasswordRequest request, ConstraintValidatorContext context) {
        return request.getNewPassword() != null && request.getNewPassword().equals(request.getNewPasswordConfirm());
    }
}
