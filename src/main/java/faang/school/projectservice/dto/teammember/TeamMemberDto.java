package faang.school.projectservice.dto.teammember;

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
public class TeamMemberDto{
        @NotNull
        Long id;
        @NotNull
        private Long team;
        @NotNull
        Long userId;
        @NotNull
        String username;
        @NotNull
        private List<String> roles;
        private String description;
        @NotNull
        private Integer accessLevel;
}