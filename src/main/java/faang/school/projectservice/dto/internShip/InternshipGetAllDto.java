package faang.school.projectservice.dto.internShip;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternshipGetAllDto {
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private InternshipStatus status;
}
