package faang.school.projectservice.retriever.project.request;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.model.Project;

public interface ProjectRetriever {
    void retrieveData(Project project, ProjectRequestDto projectRequestDto);
}
