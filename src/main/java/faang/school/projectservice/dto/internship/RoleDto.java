package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.TeamRole;

public enum RoleDto {
    OWNER,
    MANAGER,
    DEVELOPER,
    DESIGNER,
    TESTER,
    ANALYST,
    INTERN;

    public TeamRole toTeamRole () {
        return TeamRole.valueOf(this.name());
    }
}
