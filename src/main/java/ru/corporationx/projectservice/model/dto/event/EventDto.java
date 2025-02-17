package faang.school.projectservice.model.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.dto.skill.SkillDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    @NotNull
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 64, message = "title size more than 64 symbol")
    private String title;

    @NotNull
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    @NotNull
    private LocalDateTime endDate;

    @NotNull
    private Long ownerId;

    @NotNull
    @Size(max = 4096, message = "message length more than 4096 symbol")
    private String description;

    @NotNull
    @NotEmpty
    private List<SkillDto> relatedSkills;

    @Size(max = 128, message = "location length more than 128 symbol")
    private String location;

    @JsonProperty(defaultValue = "1")
    private Integer maxAttendees;
}