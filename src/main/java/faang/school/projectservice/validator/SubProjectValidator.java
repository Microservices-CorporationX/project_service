package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubProjectValidator {

    public void canBeParentProject(Project parentProject) {
        boolean canBe = parentProject.getParentProject() == null &&
                parentProject.getStatus() != ProjectStatus.COMPLETED &&
                parentProject.getStatus() != ProjectStatus.CANCELLED;
        if (!canBe) {
            throw new DataValidationException("Project can't be parent");
        }
    }

    public void shouldBePublic(Project project) {
        if (project.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new DataValidationException("Parent project is private");
        }
    }

    public void childCompleted(List<Project> projects) {
        for (Project project : projects) {
            if ((project.getStatus()).equals(ProjectStatus.COMPLETED) ||
                    (project.getStatus()).equals(ProjectStatus.CANCELLED)) {
                throw new DataValidationException("Project can't be closed");
            }
        }
    }
}
