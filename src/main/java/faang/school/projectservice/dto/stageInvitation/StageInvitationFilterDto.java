package faang.school.projectservice.dto.stageInvitation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StageInvitationFilterDto {
    private String descriptionPattern;
    private String stagePattern;
}
