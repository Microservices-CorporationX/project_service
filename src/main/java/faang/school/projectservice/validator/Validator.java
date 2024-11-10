package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;

import java.util.Objects;

public class Validator {

    public static void checkValidId(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("Неправильный id");
        }
    }

    public static void checkVisibility(ProjectVisibility parent, ProjectVisibility child) {
        if (parent == ProjectVisibility.PUBLIC && child == ProjectVisibility.PRIVATE) {
            throw new IllegalArgumentException("Подпроект не может быть приватным для публичного проекта");
        }
    }

    public static void checkProjectContainsChild(Project project) {
        if (project.getChildren() == null) {
            throw new NullPointerException("отсутствуют подпроекты");
        }
    }

    public static void checkProjectContainsSubProject(Project project, String subProjectName) {
        if (project.getName().equals(subProjectName)) {
            throw new IllegalArgumentException("Проект с таким именем уже существует");
        }

        if (project.getChildren() != null && !project.getChildren().isEmpty()) {
            project.getChildren().forEach(subProject -> checkProjectContainsSubProject(subProject, subProjectName));
        }
    }

    public static void checkSubProjectStatus(ProjectStatus projectStatus, boolean allCancelled) {
        if (projectStatus == ProjectStatus.CANCELLED && !allCancelled) {
            throw new IllegalStateException("Что бы закрыть проект нужно закрыть все подпроекты");
        }
    }
}
