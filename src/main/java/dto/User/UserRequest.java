package dto.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequest {
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String email;
}
