package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.IllegalArgumentException;
import faang.school.projectservice.filter.stage_invitation_filter.StageInvitationFilter;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.mapper.stageInvitation.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.validator.StageInvitationValidator;
import faang.school.projectservice.validator.StageValidator;
import faang.school.projectservice.validator.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationJpaRepository repository;
    private final List<StageInvitationFilter> filters;
    private final StageInvitationValidator stageInvValidator;
    private final TeamMemberValidator teamMemberValidator;
    private final StageValidator stageValidator;

    public void sendStageInvitation(long invitorId, StageInvitationDto dto) {
        stageInvValidator.validateStageInvitationExists(dto.getId());
        TeamMember author = teamMemberValidator.validateTeamMemberExists(invitorId);
        TeamMember invited = teamMemberValidator.validateTeamMemberExists(dto.getInvitedId());
        Stage stageToInvite = stageValidator.validateStageExists(dto.getStageId());

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
        StageInvitation invitation = stageInvValidator.validateStageInvitationNotExists(stageInvitationId);
        stageInvValidator.validateIsInvitationSentToThisTeamMember(invitedId, stageInvitationId);
        TeamMember teamMember = teamMemberValidator.validateIsTeamMemberParticipantOfProject(invitedId, invitation);

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        invitation.getStage().getExecutors().add(teamMember);
        log.info("Saving stage invitation with ID: {} and ACCEPTED status, for team member with ID: {}",
                stageInvitationId, invitedId);

        repository.save(invitation);
    }

    public void rejectStageInvitation(long invitedId, long stageInvitationId, String rejectReason) {
        StageInvitation invitation = stageInvValidator.validateStageInvitationNotExists(stageInvitationId);
        stageInvValidator.validateIsInvitationSentToThisTeamMember(invitedId, stageInvitationId);
        validateRejectReasonIsNullOrEmpty(rejectReason);

        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(rejectReason);
        log.info("Saving invitation with ID: {} and REJECTED status, for team member with ID: {}",
                stageInvitationId, invitedId);

        repository.save(invitation);
    }

    public List<StageInvitationDto> getStageInvitations(long invitedId, StageInvitationFilterDto filter) {
        List<StageInvitation> invitations = repository.findAll();
        Stream<StageInvitation> filteredInvitations = invitations.stream()
                .filter(invitation -> invitation.getInvited().getId().equals(invitedId));
        log.info("Founding filtered stage invitations for team member with ID: {}", invitedId);

        return filter(filteredInvitations, filter);
    }

    private List<StageInvitationDto> filter(Stream<StageInvitation> invitations, StageInvitationFilterDto filter) {
        return filters.stream()
                .filter(stageFilter -> stageFilter.isApplicable(filter))
                .flatMap(stageFilter -> stageFilter.apply(invitations, filter))
                .map(stageInvitationMapper::toDto)
                .toList();
    }

    private void validateRejectReasonIsNullOrEmpty(String rejectReason) {
        if (rejectReason == null || rejectReason.isEmpty()) {
            throw new IllegalArgumentException("Reject reason can't be empty");
        }
    }
}