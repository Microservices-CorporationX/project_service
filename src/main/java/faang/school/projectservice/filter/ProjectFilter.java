package faang.school.projectservice.filter;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Data;

@Data
public class ProjectFilter {
    private final String name;
    private final ProjectStatus status;
    private final ProjectVisibility visibility;
    private final Long userId;

    public boolean apply(Project project) {
        return (name == null || project.getName().equals(name) &&
                (status == null || project.getStatus() == status) &&
                (visibility == null || project.getVisibility() == visibility) &&
                (project.getVisibility() == ProjectVisibility.PUBLIC || project.getOwnerId().equals(userId)));
    }
}
