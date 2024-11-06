package faang.school.projectservice.service.stageInvitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.exception.AlreadyExistsException;
import faang.school.projectservice.exception.EntityNotFoundException;
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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final TeamMemberRepository teamMemberRepository;        //Create Service
    private final StageRepository stageRepository;              //Create service

    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationJpaRepository repository;

    public void sendStageInvitation(long invitorId, StageInvitationDto dto) {
        stageInvitationExistenceCheckExists(dto.getId());
        //is invited/invitor ,team member //
        TeamMember author = teamMemberRepository.findById(invitorId);
        TeamMember invited = teamMemberRepository.findById(dto.getInvitedId());
        Stage stageToInvite = stageRepository.getById(dto.getStageId()); //throws entity not found ^

        StageInvitation stageInvitation = stageInvitationMapper.toEntity(dto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        stageInvitation.setStage(stageToInvite);
        stageInvitation.setAuthor(author);
        stageInvitation.setInvited(invited);

        repository.save(stageInvitation);
    }

    public void acceptStageInvitation(long invitedId, long stageInvitationId) {
        StageInvitation invitation = stageInvitationExistenceCheckNotExists(stageInvitationId);
        validateIsInvitationSentToThisTeamMember(invitedId, stageInvitationId);
        TeamMember teamMember = validateIsTeamMemberParticipantOfProject(invitedId, invitation);

        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        invitation.getStage().getExecutors().add(teamMember);
        repository.save(invitation);
    }

    private void stageInvitationExistenceCheckExists(long id) {
        StageInvitation stageInvitation = repository.findById(id).orElse(null);

        if (stageInvitation == null) {
            //log
            throw new AlreadyExistsException("Stage Invitation with id: " + id + " already exists");
        }
    }

    private StageInvitation stageInvitationExistenceCheckNotExists(long id) {
        //log
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Stage Invitation with id: " + id + " not exists"));
    }

    private void validateIsInvitationSentToThisTeamMember(long invitedId, long stageInvitationId) {
        StageInvitation stageInvitation = stageInvitationExistenceCheckNotExists(stageInvitationId);

        if (!stageInvitation.getInvited().getId().equals(invitedId)) {
            throw new IllegalArgumentException("This stage invitation does not belong to this team member");
        }
    }

    private TeamMember validateIsTeamMemberParticipantOfProject(long invitedId, StageInvitation invitation) {
        TeamMember teamMember = teamMemberRepository.findById(invitedId);
        Project project = invitation.getStage().getProject();

        boolean isTeamMember = teamMember.getStages().stream()
                .anyMatch(stage -> stage.getProject().equals(project));

        if (!isTeamMember) {
            throw new IllegalArgumentException("This team member is not participant of this project");
        }
        return teamMember;
    }
}
