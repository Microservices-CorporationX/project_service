package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import faang.school.projectservice.model.stage.Stage;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final StageInvitationMapper stageInvitationMapper;

    public StageInvitationDto sendInvitation(Long stageId, Long authorId, Long invitedId, String description) {
        Stage stage = stageRepository.getById(stageId);
        TeamMember author = teamMemberRepository.findById(authorId);
        TeamMember invited = teamMemberRepository.findById(invitedId);
        StageInvitation stageInvitation = StageInvitation.builder()
                .invited(invited)
                .stage(stage)
                .author(author)
                .status(StageInvitationStatus.PENDING)
                .description(description)
                .build();
        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    public void acceptInvitation(Long invitationId, Long invitedId) {
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        checkInvitedId(invitation,invitedId);
        TeamMember invited = teamMemberRepository.findById(invitedId);
        Stage stage = invitation.getStage();
        invitation.setStatus(StageInvitationStatus.ACCEPTED);
        List<Stage> stages = invited.getStages();
        if (stages == null) {
            stages = new ArrayList<>();
        }
        stages.add(stage);
        invited.setStages(stages);
        List<TeamMember> executors = stage.getExecutors();
        if (executors == null) {
            executors = new ArrayList<>();
        }
        executors.add(invited);
        stage.setExecutors(executors);
        stageInvitationRepository.save(invitation);
    }

    public void declineInvitation(Long invitationId, Long invitedId, String description) {
        StageInvitation invitation = stageInvitationRepository.findById(invitationId);
        checkInvitedId(invitation, invitedId);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        StringBuilder builder = new StringBuilder(invitation.getDescription());
        builder.append(" rejected by " + invitation.getInvited().getUserId() + " with cause: " + description);
        invitation.setDescription(builder.toString());
        stageInvitationRepository.save(invitation);
    }

    private void checkInvitedId(StageInvitation invitation, Long invitedId) {
        if (!invitation.getInvited().getId().equals(invitedId)) {
            throw new DataValidationException("You can not interfere with invitation if you are not invited!");
        }
    }

    public List<StageInvitationDto> getAllInvitationsForUserWithStatus(Long teamMemberId, StageInvitationStatus status) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        List<StageInvitation> result = stageInvitationRepository.findAll().stream()
                        .filter(stageInvitation -> stageInvitation.getInvited().equals(teamMember))
                        .filter(stageInvitation -> stageInvitation.getStatus().equals(status))
                .toList();
        System.out.println(result);
        System.out.println(stageInvitationMapper.toDto(result));
        return stageInvitationMapper.toDto(result);
    }
}
