package faang.school.projectservice.model;

import lombok.Getter;

@Getter
public enum TeamMemberActions {
    ADD("adding a team member"),
    UPDATE("updating a team member"),
    REMOVE("removing a team member");

    private final String description;

    TeamMemberActions(String description) {
        this.description = description;
    }
}
