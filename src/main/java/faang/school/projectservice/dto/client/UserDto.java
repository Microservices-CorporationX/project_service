package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
    @Positive
    private Long id;
    @NotBlank(message = "Username should not be blank")
    @Size(max = 255, message = "Username should not exceed 255 characters")
    private String username;
    @NotBlank(message = "Email should not be blank")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email should not exceed 255 characters")
    private String email;
}
