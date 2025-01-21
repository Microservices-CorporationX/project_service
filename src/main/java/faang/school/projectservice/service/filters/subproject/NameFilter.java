package faang.school.projectservice.service.filters.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class NameFilter implements SubProjectFilter {
    @Override
    public boolean isApplicable(SubProjectFilterDto filters) {
        return filters.getName() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, SubProjectFilterDto filters) {
        String regex = ".*" + Pattern.quote(filters.getName()) + ".*";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        return projects
                .filter(proj -> pattern.matcher(proj.getName()).matches());
    }
}