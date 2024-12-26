package faang.school.projectservice.service;


import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ValidateService {
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    public void validateCuratorRole(Long curatorId) {
        TeamMember curator = teamMemberRepository.findById(curatorId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid curator id: " + curatorId));

        if (!curator.getRoles().contains(TeamRole.OWNER) &&
                !curator.getRoles().contains(TeamRole.MANAGER)) {
            throw new IllegalArgumentException("Curator should be OWNER or MANAGER");
        }
    }

    public void validateCandidatesNotInProject(List<Long> candidateIds, Long projectId) {
        List<Long> projectMembers = projectRepository.findAllTeamMemberIdsByProjectId(projectId);
        candidateIds.forEach(candidateId -> {
            if (projectMembers.contains(candidateId)) {
                throw new IllegalArgumentException("Candidate " + candidateId + " is already in project " + projectId);
            }
        });
    }

    public void validateVacancyClosure(Vacancy vacancy, VacancyStatus status) {
        if (status == VacancyStatus.CLOSED) {
            if (vacancy.getCandidates().isEmpty()) {
                throw new IllegalArgumentException("Vacancy has no candidates");
            }
            if (vacancy.getCandidates().size() < vacancy.getCount()) {
                throw new IllegalArgumentException("Vacancy has not enough candidates");
            }
        }
    }
}
