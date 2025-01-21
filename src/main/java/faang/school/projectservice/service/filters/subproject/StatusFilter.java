package faang.school.projectservice.service.filters.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class StatusFilter implements SubProjectFilter {
    @Override
    public boolean isApplicable(SubProjectFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, SubProjectFilterDto filters) {
        return projects
                .filter(proj -> Objects.equals(proj.getStatus(), filters.getStatus()));
    }
}