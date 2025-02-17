package faang.school.projectservice.model.dto.skill;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {
    @NotNull
    private Long id;
    @NotNull
    private String title;
}
