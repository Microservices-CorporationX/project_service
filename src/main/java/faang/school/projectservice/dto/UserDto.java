package faang.school.projectservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDto {
    private long id;
    private String username;
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