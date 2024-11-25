package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyDuplicationException;
import faang.school.projectservice.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VacancyValidator {

    public void validateSupervisorHasOwnerRole(TeamMember supervisor) {
        if (!supervisor.getRoles().contains(TeamRole.OWNER) || !supervisor.getRoles().contains(TeamRole.MANAGER)) {
            log.error("Supervisor does not have the required role: {} or {}", TeamRole.OWNER, TeamRole.MANAGER);
            throw new IllegalArgumentException("Supervisor does not have the required role");
        }
    }

    public void validateUniqueVacancy(Project project, VacancyDto vacancyDto){
        boolean isDuplicate = project.getVacancies().stream()
                .anyMatch(vacancy -> vacancy.getName().equals(vacancyDto.name()) &&
                        vacancy.getDescription().equals(vacancyDto.description()));

        if (isDuplicate) {
            log.error("Vacancy with name: {} and description: {} already exists", vacancyDto.name(), vacancyDto.description());
            throw new VacancyDuplicationException("Vacancy  already exists");
        }
    }

    public void validateCanCloseVacancy(VacancyDto vacancyDto, int requiredCandidates, int numberOfCandidates) {
        if (vacancyDto.status() == VacancyStatus.CLOSED && requiredCandidates > numberOfCandidates) {
            log.error("Cannot close vacancy with id: {}. Required: {}, Found: {}", vacancyDto.id(), requiredCandidates, numberOfCandidates);
            throw new IllegalArgumentException("Cannot close vacancy. Required number of candidates not met.");
        }
    }
}
