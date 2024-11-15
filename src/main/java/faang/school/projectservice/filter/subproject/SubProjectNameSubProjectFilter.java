package faang.school.projectservice.filter.subproject;

import com.amazonaws.util.StringUtils;
import faang.school.projectservice.dto.project.FilterProjectDto;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SubProjectNameSubProjectFilter implements SubProjectFilter<FilterProjectDto, Project> {
    @Override
    public boolean isApplicable(FilterProjectDto filterDto) {
        return !StringUtils.isNullOrEmpty(filterDto.getName());
    }

    @Override
    public Stream<Project> apply(Stream<Project> itemStream, FilterProjectDto filterDto) {
        return itemStream.filter(project -> project.getName().contains(filterDto.getName()));
    }
}
