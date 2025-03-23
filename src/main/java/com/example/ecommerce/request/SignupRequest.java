package com.example.ecommerce.request;

import com.example.ecommerce.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank(message = "A felhasználónév megadása kötelező.")
    private String username;
    @NotBlank(message = "A jelszó megadása kötelező.")
    @Size(min = 8, max = 64, message = "A jelszónak legalább 8, de legfeljebb 64 karakter hosszúnak kell lennie.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "A jelszónak tartalmaznia kell kis- és nagybetűt, számot és speciális karaktert (@$!%*?&)."
    )
    private String password;
    @NotBlank(message = "Az email cím megadása kötelező.")
    @Email(message = "Érvénytelen email cím formátum.")
    private String email;
    @NotBlank(message = "A jogosultság megadása kötelező.")
    private String role;
}
