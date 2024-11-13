package faang.school.projectservice.filter;

import faang.school.projectservice.dto.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubProjectNameFilter implements SubProjectFilter {

    @Override
    public boolean isApplicable(SubProjectFilterDto subProjectFilterDto) {
        return subProjectFilterDto.name() != null;
    }

    @Override
    public List<Project> apply(List<Project> projects,
                               SubProjectFilterDto subProjectFilterDto) {
        return projects.stream()
                .filter(project -> project.getName().equals(subProjectFilterDto.name()))
                .toList();
    }
}
