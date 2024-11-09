package faang.school.projectservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MomentFilterDto {
    private LocalDateTime datePattern;
    private String projectsPattern;
}
