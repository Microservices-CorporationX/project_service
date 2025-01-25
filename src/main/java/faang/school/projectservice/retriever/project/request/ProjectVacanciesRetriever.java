package faang.school.projectservice.retriever.project.request;

import faang.school.projectservice.dto.project.ProjectRequestDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectVacanciesRetriever implements ProjectRetriever {
    private final VacancyService vacancyService;

    @Override
    public void retrieveData(Project project, ProjectRequestDto projectRequestDto) {
        project.setVacancies(vacancyService.getVacanciesByIds(projectRequestDto.getVacanciesIds()));
    }
}
