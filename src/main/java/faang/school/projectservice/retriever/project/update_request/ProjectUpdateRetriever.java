package faang.school.projectservice.retriever.project.update_request;

import faang.school.projectservice.dto.project.ProjectUpdateRequestDto;
import faang.school.projectservice.model.Project;

public interface ProjectUpdateRetriever {
    void retrieveData(Project project, ProjectUpdateRequestDto projectUpdateRequestDto);
}
