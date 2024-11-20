package faang.school.projectservice.service.teammember;

import faang.school.projectservice.dto.team_member.TeamMemberDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.team_member.TeamMemberMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.validator.team_member.TeamMemberValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamMemberService {

    private static final String TEAM_MEMBER = "TeamMember";

    private final TeamMemberJpaRepository teamMemberRepository;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamMemberValidator teamMemberValidator;

    public TeamMemberDto addMemberToTheTeam(TeamMemberDto teamMemberDto) {
        TeamMember teamMember = teamMemberMapper.toEntity(teamMemberDto);


        return teamMemberMapper.toDto(teamMember);
    }

    public TeamMemberDto getMemberById(Long id) {
        log.debug("Retrieving team member by ID: {}", id);
        teamMemberValidator.validationOnNullLessThanOrEqualToZero(id, "Id null or less than zero");
        TeamMember teamMember = findById(id);
        log.info("Team member successfully retrieved, ID: {}", id);
        return teamMemberMapper.toDto(teamMember);
    }

    public TeamMember findById(long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TEAM_MEMBER, id));
    }

    @Transactional
    public TeamMember save(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }

    @Transactional
    public List<TeamMember> saveAll(List<TeamMember> teamMembers) {
        return teamMemberRepository.saveAll(teamMembers);
    }

    public TeamMember findByUserIdAndProjectId(long userId, long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new EntityNotFoundException(TEAM_MEMBER, userId));
    }

    @Transactional
    public void delete(TeamMember teamMember) {
        teamMemberRepository.delete(teamMember);
    }

    @Transactional
    public void deleteAll(List<TeamMember> teamMembers) {
        teamMemberRepository.deleteAll(teamMembers);
    }
}
