package faang.school.projectservice.validation;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.validation.ValidationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Data
@Component
public class ValidationVacancies {
    private final static List<TeamRole> LIST_RESPONSIBLE_ROLES = List.of(TeamRole.MANAGER, TeamRole.OWNER);
    private final ProjectRepository projectRepository;
    private final VacancyRepository vacancyRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void hasToBeNotNull(String parameterValue, String nameFieldForMessage) {
        if (parameterValue == null) {
            log.error(String.format("%s can not be null. Current is: null", nameFieldForMessage));
            throw new ValidationException(String.format("%s can not be null. Current is: null", nameFieldForMessage));
        }
    }

    public void hasToBeNotNull(Long parameterValue, String nameFieldForMessage) {
        if (parameterValue == null) {
            log.error(String.format("%s can not be null. Current is: null", nameFieldForMessage));
            throw new ValidationException(String.format("%s can not be null. Current is: null", nameFieldForMessage));
        }
    }

    public void hasToBeNotNull(Integer parameterValue, String nameFieldForMessage) {
        if (parameterValue == null) {
            log.error(String.format("%s can not be null. Current is: null", nameFieldForMessage));
            throw new ValidationException(String.format("%s can not be null. Current is: null", nameFieldForMessage));
        }
    }

    public void hasToBeNull(Long parameterValue, String nameFieldForMessage) {
        if (parameterValue != null) {
            log.error(String.format("%s has to be null or absent. Current is: %s", nameFieldForMessage, parameterValue));
            throw new ValidationException(String.format("%s has to be null or absent. Current is: %s",
                    nameFieldForMessage,
                    parameterValue));
        }
    }

    public void hasToBeNull(List<Long> parameterValue, String nameFieldForMessage) {
        if (parameterValue != null) {
            log.error(String.format("%s has to be null or absent. Current is: %s", nameFieldForMessage, parameterValue));
            throw new ValidationException(String.format("%s has to be null or absent. Current is: %s",
                    nameFieldForMessage,
                    parameterValue));
        }
    }

    public void isProjectExist(Long idProject) {
        if (idProject <= 0 || !projectRepository.existsById(idProject)) {
            log.error(String.format("There is not project with id %s", idProject));
            throw new ValidationException(String.format("There is not project with id %s", idProject));
        }
    }

    public void isVacancyExist(Long idVacancy) {
        if (!vacancyRepository.existsById(idVacancy)) {
            log.error(String.format("There is not vacancy with id %s", idVacancy));
            throw new ValidationException(String.format("There is not vacancy with id %s", idVacancy));
        }
    }

    public void personHasNecessaryRole(Long idPersonBy, String nameParameter, Long projectId) {
        if (idPersonBy != null){
            TeamMember teamMember = teamMemberRepository.getJpaRepository().findByUserIdAndProjectId(idPersonBy, projectId);
            if (teamMember == null || teamMember.getRoles() == null || teamMember.getRoles().stream()
                                                                        .noneMatch(LIST_RESPONSIBLE_ROLES::contains)) {
                log.error(String.format("Person: %s does not have a Role %s",
                        nameParameter,
                        LIST_RESPONSIBLE_ROLES));

                throw new ValidationException(String.format("Person: %s does not have a Role %s",
                        nameParameter,
                        LIST_RESPONSIBLE_ROLES));
            }
        }
    }

    public void numberCandidatesForCloser(List<Candidate> candidateList, Integer necessaryNumberCandidates) {
        if (candidateList == null ||
                necessaryNumberCandidates == null ||
                necessaryNumberCandidates > candidateList.size()) {
            log.error(String.format("Number candidates is %s, should be min %s",
                    candidateList == null ? null : candidateList.size(),
                    necessaryNumberCandidates));
            throw new ValidationException(String.format("Number candidates is %s, should be min %s",
                    candidateList == null ? null : candidateList.size(),
                    necessaryNumberCandidates));
        }
    }
    //--ToDo
/*
    private void hasToBePersonExist(Long idPersonBy, Long projectId, String nameParameterForMessage) {
        if (idPersonBy == null ||
                teamMemberRepository.getJpaRepository().findByUserIdAndProjectId(idPersonBy, projectId) == null) {
            log.error(String.format("%s person does not exist. idCreatedBy: %s",
                    nameParameterForMessage,
                    idPersonBy));

            throw new ValidationException(String.format("%s person does not exist. idCreatedBy: %s",
                    nameParameterForMessage,
                    idPersonBy));
        }
    }
 */
}