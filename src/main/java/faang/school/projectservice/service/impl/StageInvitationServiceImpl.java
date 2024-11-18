package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.invitation.RejectionDto;
import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.filters.abstracts.StageInvitationFilter;
import faang.school.projectservice.jpa.StageInvitationJpaRepository;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.abstracts.StageInvitationService;
import faang.school.projectservice.service.abstracts.StageService;
import faang.school.projectservice.service.abstracts.TeamMemberService;
import faang.school.projectservice.validator.abstracts.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageInvitationServiceImpl implements StageInvitationService {
    private final StageInvitationJpaRepository invitationRepository;
    private final TeamMemberService teamMemberService;
    private final StageService stageService;
    private final StageInvitationMapper invitationMapper;
    private final StageInvitationValidator invitationValidator;
    private final List<StageInvitationFilter> invitationFilters;

    @Override
    public StageInvitationDto sendStageInvitation(StageInvitationDto stageInvitationDto) {
        log.info("method: sendStageInvitation; input data: {}", stageInvitationDto);

        Long authorId = stageInvitationDto.getAuthorId();
        Long invitedId = stageInvitationDto.getInvitedId();
        Long stageId = stageInvitationDto.getStageId();
        invitationValidator.validateAuthorAndInvited(authorId, invitedId);

        TeamMember author = teamMemberService.findById(authorId);
        TeamMember invited = teamMemberService.findById(invitedId);
        Stage stage = stageService.findById(stageId);
        invitationValidator.validateInvitationDoesNotExist(invited, stage);

        StageInvitation stageInvitation = StageInvitation.builder()
                .status(StageInvitationStatus.PENDING)
                .stage(stage)
                .author(author)
                .invited(invited)
                .build();
        invitationRepository.save(stageInvitation);

        log.info("successful invitation creation with id: {}", stageInvitation.getId());
        return invitationMapper.toDto(stageInvitation);
    }

    @Override
    public StageInvitationDto acceptStageInvitation(Long invitationId) {
        log.info("method: acceptStageInvitation; input data: {}", invitationId);

        StageInvitation stageInvitation = invitationValidator.getStageInvitation(invitationId);
        invitationValidator.validateInvitationStatus(stageInvitation, StageInvitationStatus.ACCEPTED);
        if (stageInvitation.getStatus().equals(StageInvitationStatus.REJECTED)) {
            stageInvitation.setDescription(null);
        }

        TeamMember invited = stageInvitation.getInvited();
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitation.getStage().getExecutors().add(invited);
        invitationRepository.save(stageInvitation);

        log.info("successful acceptance of an invitation with id: {}", invitationId);
        return invitationMapper.toDto(stageInvitation);
    }

    @Override
    public StageInvitationDto rejectStageInvitation(Long invitationId, RejectionDto rejectionDto) {
        log.info("method: rejectStageInvitation; input data: {}, {}", invitationId, rejectionDto);

        StageInvitation stageInvitation = invitationValidator.getStageInvitation(invitationId);
        invitationValidator.validateInvitationStatus(stageInvitation, StageInvitationStatus.REJECTED);
        if (stageInvitation.getStatus().equals(StageInvitationStatus.ACCEPTED)) {
            TeamMember invited = stageInvitation.getInvited();
            stageInvitation.getStage().getExecutors().remove(invited);
        }

        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setDescription(rejectionDto.getReason());
        invitationRepository.save(stageInvitation);

        log.info("successful rejection of request with id: {}", invitationId);
        return invitationMapper.toDto(stageInvitation);
    }

    @Override
    public List<StageInvitationDto> getInvitations(Long invitedId, StageInvitationFilterDto filterDto) {
        log.info("method: getInvitations; input data: {}, {}", invitedId, filterDto);

        TeamMember invited = teamMemberService.findById(invitedId);
        Stream<StageInvitation> invitationStream = invitationRepository
                .findStageInvitationsByInvited(invited).stream();

        Stream<StageInvitation> filteredInvitations = invitationFilters.stream()
                .filter(invitationFilter -> invitationFilter.isApplicable(filterDto))
                .reduce(invitationStream,
                        (subtotalStream, invitationFilter) -> invitationFilter.apply(subtotalStream, filterDto),
                        (firstSubtotalStream, secondSubtotalStream) -> firstSubtotalStream);

        log.info("successful get invitations by invitedId: {}", invitedId);
        return filteredInvitations
                .map(invitationMapper::toDto)
                .toList();
    }
}