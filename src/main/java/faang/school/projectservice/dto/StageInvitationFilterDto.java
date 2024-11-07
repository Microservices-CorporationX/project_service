package faang.school.projectservice.dto;

import lombok.Builder;

@Builder
public record StageInvitationFilterDto(
        Long stageId,
        Long authorId
) {
}
