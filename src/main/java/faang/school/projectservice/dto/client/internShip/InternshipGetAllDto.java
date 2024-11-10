package faang.school.projectservice.dto.client.internShip;

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
    private String description;

    @NotNull
    private Long mentorId;

    @NotNull
    private LocalDateTime startDate;
}
