package faang.school.projectservice.dto.teammember;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberDto {
        @NotNull
        private Long id;

        @NotNull
        private Long team;

        @NotNull
        private Long userId;

        @NotNull
        private String username;

        @NotNull
        private List<String> roles;

        private String description;

        private Integer accessLevel;
}
