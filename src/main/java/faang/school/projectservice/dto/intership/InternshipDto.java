package faang.school.projectservice.dto.intership;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.InternshipStatus;
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
public class InternshipDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private Long mentorId;
    private List<Long> internIds;
    private InternshipStatus internshipStatus;
    private Long createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;
}
