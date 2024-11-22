package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.subprojectDto.subprojectFilterDto.SubprojectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubprojectNameFilter implements SubprojectFilter {
    @Override
    public boolean isApplicable(SubprojectFilterDto filters) {
        return filters.getName() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, SubprojectFilterDto filters) {
        if (projects == null) throw new IllegalArgumentException("Projects cannot be null");

        boolean hasVisibleProjects = projects.anyMatch(project ->
                project.getVisibility() != ProjectVisibility.PRIVATE
        );

        if (!hasVisibleProjects) {
            throw new IllegalArgumentException("Private projects are not allowed");
        }

        return projects.filter(project ->
                project.getChildren() != null &&
                        project.getChildren().stream().anyMatch(subproject ->
                                subproject.getName() != null &&
                                        subproject.getName().contains(filters.getName())
                        )
        );
    }


}
