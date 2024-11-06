package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.AlreadyExistsException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.IllegalArgumentException;
import faang.school.projectservice.filter.stage_invitation_filter.StageInvitationFilter;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.mapper.stageInvitation.StageInvitationMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final TeamMemberRepository teamMemberRepository;        //Create Service
    private final StageRepository stageRepository;              //Create service

    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationJpaRepository repository;
    private final List<StageInvitationFilter> filters;


    public void sendStageInvitation(long invitorId, StageInvitationDto dto) {
        validateStageInvitationExists(dto.getId());
        //is invited/invitor ,team member //
        TeamMember author = teamMemberRepository.findById(invitorId);
        TeamMember invited = teamMemberRepository.findById(dto.getInvitedId());
        Stage stageToInvite = stageRepository.getById(dto.getStageId()); //throws entity not found ^

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(dto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitation.setStage(stageToInvite);
        stageInvitation.setAuthor(author);
        stageInvitation.setInvited(invited);
        log.info("Saving new stage invitation with PENDING status, for team member with ID: {}", dto.getInvitedId());

        repository.save(stageInvitation);
    }

    public void acceptStageInvitation(long invitedId, long stageInvitationId) {
        StageInvitation invitation = validateStageInvitationNotExists(stageInvitationId);
        validateIsInvitationSentToThisTeamMember(invitedId, stageInvitationId);
        TeamMember teamMember = validateIsTeamMemberParticipantOfProject(invitedId, invitation);

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        invitation.getStage().getExecutors().add(teamMember);
        log.info("Saving stage invitation with ID: {} and ACCEPTED status, for team member with ID: {}",
                stageInvitationId, invitedId);

        repository.save(invitation);
    }

    public void rejectStageInvitation(long invitedId, long stageInvitationId, String rejectReason) {
        StageInvitation invitation = validateStageInvitationNotExists(stageInvitationId);
        validateIsInvitationSentToThisTeamMember(invitedId, stageInvitationId);
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

    private void validateStageInvitationExists(long id) {
        StageInvitation stageInvitation = repository.findById(id).orElse(null);

        if (stageInvitation == null) {
            throw new AlreadyExistsException("Stage Invitation with id: " + id + " already exists");
        }
    }

    private StageInvitation validateStageInvitationNotExists(long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Stage Invitation with id: " + id + " not exists"));
    }

    private void validateIsInvitationSentToThisTeamMember(long invitedId, long stageInvitationId) {
        StageInvitation stageInvitation = validateStageInvitationNotExists(stageInvitationId);

        if (!stageInvitation.getInvited().getId().equals(invitedId)) {
            throw new IllegalArgumentException("This stage invitation does not belong to this team member");
        }
    }

    private TeamMember validateIsTeamMemberParticipantOfProject(long invitedId, StageInvitation invitation) {
        TeamMember teamMember = teamMemberRepository.findById(invitedId);
        Project projectOfTeamMember = invitation.getStage().getProject();

        boolean isParticipant = teamMember.getStages().stream()
                .anyMatch(stage -> stage.getProject().equals(projectOfTeamMember));

        if (!isParticipant) {
            throw new IllegalArgumentException("This team member is not participant of this project");
        }
        return teamMember;
    }

    private void validateRejectReasonIsNullOrEmpty(String rejectReason) {
        if (rejectReason == null || rejectReason.isEmpty()) {
            throw new IllegalArgumentException("Reject reason can't be empty");
        }
    }
}