package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.subprojectdto.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class SubProjectStatusFilter implements SubProjectFilter {

    @Override
    public boolean isApplicable(SubProjectFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public List<Project> apply(List<Project> projects, SubProjectFilterDto filter) {
        return projects.stream()
                .filter(project -> project.getStatus() == filter.getStatus())
                .collect(Collectors.toList());
    }
}
