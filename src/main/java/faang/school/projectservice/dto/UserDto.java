package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDto {

    private Long id;

    @NotBlank(message = "Username must not be blank")
    @Size(max = 128, message = "Username must not exceed 128 characters")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Size(max = 256, message = "Email must not exceed 256 characters")
    private String email;
    private boolean active;
    private String aboutMe;
    private String country;
    private Integer experience;
    private LocalDateTime createdAt;
    private List<Long> followersIds;
    private List<Long> followeesIds;
    private List<Long> menteesIds;
    private List<Long> mentorsIds;
    private List<Long> goalsIds;
    private List<Long> skillsIds;
}