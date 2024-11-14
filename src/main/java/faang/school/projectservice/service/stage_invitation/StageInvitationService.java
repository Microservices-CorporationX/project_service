package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.stage_invitation_filter.StageInvitationFilter;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.mapper.stageInvitation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.service.team_member.TeamMemberService;
import faang.school.projectservice.validator.stage_invitation.StageInvitationValidator;
import faang.school.projectservice.validator.team_member.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final List<StageInvitationFilter> filters;
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationJpaRepository repository;
    private final StageInvitationValidator stageInvValidator;
    private final TeamMemberValidator teamMemberValidator;
    private final TeamMemberService teamMemberService;
    private final StageService stageService;

    public void sendStageInvitation(long invitorId, StageInvitationDto dto) {
        TeamMember author = teamMemberService.getTeamMemberEntity(invitorId);
        TeamMember invited = teamMemberService.getTeamMemberEntity(dto.getInvitedId());
        Stage stageToInvite = stageService.getStageEntity(dto.getStageId());

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(dto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitation.setStage(stageToInvite);
        stageInvitation.setAuthor(author);
        stageInvitation.setInvited(invited);
        log.info("Saving new stage invitation with PENDING status, for team member with ID: {}",
                dto.getInvitedId());

        repository.save(stageInvitation);
    }

    public void acceptStageInvitation(long invitedId, long stageInvitationId) {
        StageInvitation invitation = getStageInvitation(stageInvitationId);
        stageInvValidator.validateIsInvitationSentToThisTeamMember(invitedId, invitation);

        TeamMember teamMember = teamMemberService.getTeamMemberEntity(invitedId);
        teamMemberValidator.validateIsTeamMemberParticipantOfProject(teamMember, invitation);

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        invitation.getStage().getExecutors().add(teamMember);
        log.info("Saving stage invitation with ID: {} and ACCEPTED status, for team member with ID: {}",
                stageInvitationId, invitedId);

        repository.save(invitation);
    }

    public void rejectStageInvitation(long invitedId, long stageInvitationId, String rejectReason) {
        StageInvitation invitation = getStageInvitation(stageInvitationId);

        validateRejectReasonIsNullOrEmpty(rejectReason);
        stageInvValidator.validateIsInvitationSentToThisTeamMember(invitedId, invitation);

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(rejectReason);
        log.info("Saving invitation with ID: {} and REJECTED status, for team member with ID: {}",
                stageInvitationId, invitedId);

        repository.save(invitation);
    }

    public List<StageInvitationDto> getStageInvitations(long invitedId, StageInvitationFilterDto filter) {
        List<StageInvitation> invitations = repository.findAll();
        Stream<StageInvitation> invitationsForUser = invitations.stream()
                .filter(invitation -> invitation.getInvited().getId().equals(invitedId));

        log.info("Founding filtered stage invitations for team member with ID: {}", invitedId);
        return filter(invitationsForUser, filter);
    }

    private List<StageInvitationDto> filter(Stream<StageInvitation> invitations, StageInvitationFilterDto filter) {
        return filters.stream()
                .filter(stageFilter -> stageFilter.isApplicable(filter))
                .reduce(invitations,
                        (invStream, stageFilter) -> stageFilter.apply(invStream, filter),
                        (a, b) -> b
                )
                .map(stageInvitationMapper::toDto)
                .toList();
    }

    private void validateRejectReasonIsNullOrEmpty(String rejectReason) {
        if (rejectReason == null || rejectReason.isBlank()) {
            throw new DataValidationException("Reject reason can't be empty");
        }
    }

    private StageInvitation getStageInvitation(long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("StageInvitation", id));
    }
}