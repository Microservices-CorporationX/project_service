package faang.school.projectservice.dto.client.teammember;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TeamMemberDto(
        @NotNull(groups = {After.class})
        Long id,
        @NotNull(groups = {Before.class})
        Long userId,
        @NotNull
        String username,
        @NotNull
        String role
) {
        public interface Before {}
        public interface After {}
}
