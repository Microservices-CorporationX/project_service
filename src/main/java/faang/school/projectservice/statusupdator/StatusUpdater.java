package faang.school.projectservice.statusupdator;

import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;

public interface StatusUpdater {

    public boolean isApplicable(UpdateSubProjectDto updateSubProjectDto);

    public void changeStatus(Project project);
}
