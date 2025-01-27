package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CreateMomentRequest(@NotBlank(message = "Название момента не может быть пустым")
                                  String name,
                                  String description,
                                  LocalDateTime date,
                                  @NotNull(message = "Момент должен относиться хотя бы к одному проекту")
                                  List<Long> projectIds,
                                  List<Long> resourceIds,
                                  List<Long> userIds,
                                  String imageId) {
}
