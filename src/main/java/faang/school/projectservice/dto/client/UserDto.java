package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Positive
    private Long id;

    @NotBlank(message = "Username should not be blank")
    @Size(max = 255, message = "Username should not exceed 255 characters")
    private String username;
    private String aboutMe;

    @NotBlank(message = "Email should not be blank")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email should not exceed 255 characters")
    private String email;

    private List<Long> menteeIds;
    private List<Long> mentorIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
