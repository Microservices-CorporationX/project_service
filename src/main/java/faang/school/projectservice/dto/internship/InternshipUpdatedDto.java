package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    private Long id;

    private String name;

    private Long projectId;

    @NotNull(message = "Internship status can not be null")
    private InternshipStatus status;

    @NotEmpty(message = "Interns list cannot be empty")
    private List<@Positive Long> interns;
    private Long createdBy;
}
