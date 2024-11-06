package faang.school.projectservice.dto.stageInvitation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationFilterDto {
    private String descriptionPattern;
    private String stageNamePattern;
    private String projectNamePattern;
}
