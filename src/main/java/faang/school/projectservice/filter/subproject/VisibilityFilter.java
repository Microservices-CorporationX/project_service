package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.project.FilterProjectDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VisibilityFilter implements Filter<FilterProjectDto, Project> {
    @Override
    public boolean isApplicable(FilterProjectDto filterDto) {
        return filterDto.getVisibility() != null && filterDto.getVisibility() != ProjectVisibility.PRIVATE;
    }

    @Override
    public Stream<Project> apply(Stream<Project> itemStream, FilterProjectDto filterDto) {
        return itemStream.filter(project -> project.getVisibility().equals(filterDto.getVisibility()));
    }
}
