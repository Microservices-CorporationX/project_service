package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InternshipUpdatedDto {
    @PositiveOrZero
    private Long id;

    private Long projectId;

    @NotNull(message = "Internship status can not be null")
    private InternshipStatus status;

    private List<@Positive Long> interns;
}
