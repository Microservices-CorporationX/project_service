package faang.school.projectservice.dto.intership;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternshipDto {
    @Positive
    private Long id;
    @NotBlank(message = "Name should not be blank")
    @Size(max = 255, message = "Name should not exceed 255 characters")
    private String name;
    @NotBlank(message = "Description should not be blank")
    @Size(max = 255, message = "Description should not exceed 255 characters")
    private String description;
    private Long projectId;
    private Long mentorId;
    private List<@NonNull Long> internIds;
    private InternshipStatus internshipStatus;
    private Long createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;
}
