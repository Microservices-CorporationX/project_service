package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetDto {

    private Long id;

    @NotBlank(message = "Title should not be blank", groups = {Before.class, After.class})
    @Size(max = 128, message = "Title's length should not be greater than 128 characters", groups = {Before.class, After.class})
    private String title;

    @NotBlank(message = "Description should not be blank", groups = {Before.class, After.class})
    @Size(max = 512, message = "Description's length should not be greater than 512 characters", groups = {Before.class, After.class})
    private String description;

    @NotNull(message = "The meeting status cannot be null", groups = After.class)
    private MeetStatus status;

    @NotNull(groups = {Before.class, After.class})
    private Long creatorId;

    @NotNull(groups = {Before.class, After.class})
    private Long projectId;

    @NotEmpty(message = "The list of user IDs cannot be empty", groups = {Before.class, After.class})
    private List<Long> userIds;

    @NotNull
    @FutureOrPresent(message = "Meet date cannot be in the past")
    private LocalDateTime meetDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public interface Before {}

    public interface After {}
}
