package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipFilterDto {
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Internship status can not be null")
    private InternshipStatus internshipStatus;

    @Enumerated(EnumType.STRING)
    private TeamRole teamRole;
}
