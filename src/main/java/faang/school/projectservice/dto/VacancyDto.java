package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class VacancyDto {
    private Long id;
    private String name;
    private String description;
    private Integer positionId;
    private Long projectId;
    private Double salary;
    private String coverImageKey;
    private Long curatorId;
    private Integer curatorRoleId;
    private List<Long> requiredSkillIds;
    private Integer count;
    private List<Long> candidatesIds;
    private Integer statusId;
}

