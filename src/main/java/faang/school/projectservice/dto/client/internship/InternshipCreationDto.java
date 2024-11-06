package faang.school.projectservice.dto.client.internship;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
public class InternshipCreationDto {

    @NotNull
    private Long projectId;

    @NotNull(message = "The internship team must have a mentor!")
    private Long mentorUserId;

    @NotNull(message = "The created internship must have a creator!")
    private Long creatorUserId;

    @NotNull(message = "The list of interns cannot be null!")
    @NotEmpty(message = "The list of interns cannot be empty!")
    private List<@NotNull Long> internUserIds;

    @NotNull(message = "The internship description cannot be null!")
    @NotBlank
    private String description;

    @NotNull(message = "The internship start date cannot be null!")
    @Future(message = "The internship start date must be in the future!")
    private LocalDateTime startDate;

    @NotNull(message = "The internship end date cannot be null!")
    @Future(message = "The internship end date must be in the future!")
    private LocalDateTime endDate;
}
