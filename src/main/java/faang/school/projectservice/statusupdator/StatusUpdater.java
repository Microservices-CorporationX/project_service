package faang.school.projectservice.statusupdator;

import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;

public interface StatusUpdater {

    boolean isApplicable(UpdateSubProjectDto updateSubProjectDto);

    void changeStatus(Project project);
}
