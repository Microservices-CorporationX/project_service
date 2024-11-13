package faang.school.projectservice.dto.internShip;

import faang.school.projectservice.model.InternshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InternshipGetByIdDto {
    private Long id;
    private String title;
    private String description;
    private InternshipStatus status;
}
