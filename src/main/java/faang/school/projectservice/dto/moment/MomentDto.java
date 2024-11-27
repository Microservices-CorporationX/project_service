package faang.school.projectservice.dto.moment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class MomentDto {
    @NotBlank(message = "Moment Name cannot be null or empty")
    private String name;

    @NotBlank(message = "Moment Description cannot be null or empty")
    private String description;

    @NotNull(message = "Moment date is not provided")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    private String imageId;

    @NotNull(message = "Project list must contain at least one project ID")
    @Size(min = 1, message = "Project list must contain at least one project ID")
    private List<Long> projectIds;

    private List<Long> teamMemberIds;
}
