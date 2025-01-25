package faang.school.projectservice.retriever.project.create_request;

import faang.school.projectservice.dto.project.ProjectCreateRequestDto;
import faang.school.projectservice.model.Project;

public interface ProjectCreateRetriever {
    void retrieveData(Project project, ProjectCreateRequestDto projectCreateRequestDto);
}
