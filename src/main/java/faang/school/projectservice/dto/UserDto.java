package faang.school.projectservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {

    private Long id;

    @NotBlank(message = "Username must not be blank")
    @Size(max = 128, message = "Username must not exceed 128 characters")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Size(max = 256, message = "Email must not exceed 256 characters")
    private String email;
}
