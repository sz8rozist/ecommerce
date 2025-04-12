package com.example.ecommerce.request;

import com.example.ecommerce.validators.PasswordMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatch
public class ResetPasswordRequest {
    private String token;
    @NotBlank(message = "A régi jelszó megadása kötelező.")
    private String oldPassword;
    @Size(min = 8, max = 64, message = "A jelszónak legalább 8, de legfeljebb 64 karakter hosszúnak kell lennie.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "A jelszónak tartalmaznia kell kis- és nagybetűt, számot és speciális karaktert (@$!%*?&)."
    )
    @NotBlank(message = "Az új jelszó megadása kötelező.")
    private String newPassword;
    @Size(min = 8, max = 64, message = "A jelszónak legalább 8, de legfeljebb 64 karakter hosszúnak kell lennie.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "A jelszónak tartalmaznia kell kis- és nagybetűt, számot és speciális karaktert (@$!%*?&)."
    )
    @NotBlank(message = "Az új jelszó megerősítése kötelező.")
    private String newPasswordConfirm;
}
