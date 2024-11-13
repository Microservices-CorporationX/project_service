package faang.school.projectservice.filter;

import faang.school.projectservice.dto.SubProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.List;

public interface SubProjectFilter {

    boolean isApplicable(SubProjectFilterDto subProjectFilterDto);

    List<Project> apply(List<Project> projects,
                                      SubProjectFilterDto subProjectFilterDto);

}
