package faang.school.projectservice.dto.teammember;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TeamMemberUpdateDto {

    @NotNull
    private Long currentUserId;

    @NotNull
    private Long updateUserId;

    private String nickname;

    private String description;

    private List<String> roles;

    @NotNull
    private Long projectId;

    @NotNull
    private Long teamId;

}
