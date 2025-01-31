package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.subprojectdto.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import java.util.List;


public interface SubProjectFilter {
    boolean isApplicable(SubProjectFilterDto filter);

    List<Project> apply(List<Project> projects, SubProjectFilterDto filter);
}
