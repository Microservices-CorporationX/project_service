package faang.school.projectservice.dto.teamMember;

import faang.school.projectservice.dto.internship.RoleDto;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record TeamMemberDto(
        @Nullable Long id,
        @NotNull Long userId,
        @NotNull List<RoleDto> roles,
        @Nullable Long teamId
) {

}
