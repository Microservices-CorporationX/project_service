package faang.school.projectservice.service;

import faang.school.projectservice.dto.stageinvitation.ChangeStatusDto;
import faang.school.projectservice.dto.stageinvitation.RejectInvitationDto;
import faang.school.projectservice.dto.stageinvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageinvitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.filter.stageinvitation.StageInvitationFilter;
import faang.school.projectservice.mapper.ChangeStatusMapper;
import faang.school.projectservice.mapper.RejectInvitationMapper;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.validator.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationMapper stageInvitationMapper;
    private final StageService stageService;
    private final TeamMemberService teamMemberService;
    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationValidator stageInvitationValidator;
    private final ChangeStatusMapper changeStatusMapper;
    private final RejectInvitationMapper rejectInvitationMapper;
    private final List<StageInvitationFilter> stageInvitationFilters;

    public StageInvitationDto createStageInvitation(StageInvitationDto dto) {
        StageInvitation stageInvitation = stageInvitationValidator.validateStageInvitation(dto);

        if (stageInvitationRepository.existsById(stageInvitation.getId())) {
            throw new BusinessException("Такое приглашение уже существует");
        }

        stageInvitation.setStage(stageService.getStage(dto.getStageId()));
        stageInvitation.setAuthor(teamMemberService.getTeamMember(dto.getAuthorId()));
        stageInvitation.setInvited(teamMemberService.getTeamMember(dto.getInvitedId()));
        stageInvitation.setStatus(StageInvitationStatus.PENDING);

        return stageInvitationMapper.toDto(stageInvitationRepository.save(stageInvitation));
    }

    public ChangeStatusDto acceptStageInvitation(ChangeStatusDto dto) {

        StageInvitation stageInvitation = findPendingStageInvitation(dto.getId());

        List<Stage> stages = teamMemberService
                .getTeamMember(dto.getInvitedId())
                .getStages();

        Stage stage = stageInvitation.getStage();

        if (stages.contains(stage)) {
            throw new BusinessException("Пользователь уже является исполнителем этого этапа");
        }

        stages.add(stage);
        updateStatus(stageInvitation, dto, StageInvitationStatus.ACCEPTED);

        return changeStatusMapper.toDto(stageInvitation);
    }

    public RejectInvitationDto rejectStageInvitation(RejectInvitationDto dto) {

        StageInvitation stageInvitation = findPendingStageInvitation(dto.getStatusDto().getId());
        updateStatus(stageInvitation, dto.getStatusDto(), StageInvitationStatus.REJECTED);

        return rejectInvitationMapper.toDto(stageInvitation);
    }

    public List<StageInvitationDto> getStageInvitationForTeamMember(Long invitedId) {

        Stream<StageInvitation> stageInvitationStream = stageInvitationRepository
                .findAll()
                .stream();

        StageInvitationFilterDto filterDto = StageInvitationFilterDto
                .builder()
                .invitedId(invitedId)
                .build();

        stageInvitationFilters.stream()
                .filter(invitationFilter -> invitationFilter.isApplicable(filterDto))
                .forEach(invitationFilter -> invitationFilter.apply(stageInvitationStream, filterDto));

        return stageInvitationStream.map(stageInvitationMapper::toDto).toList();
    }

    private void updateStatus(StageInvitation stageInvitation,
                              ChangeStatusDto statusDto,
                              StageInvitationStatus status) {

        statusDto.setStatus(status);
        changeStatusMapper.update(stageInvitation, statusDto);
    }

    private StageInvitation findPendingStageInvitation(Long stageInvitationId) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationId)
                .orElseThrow(() -> new BusinessException("Такого приглашения не существует"));

        if (stageInvitation.getStatus() != StageInvitationStatus.PENDING) {
            throw new BusinessException("Такое приглашение уже рассмотрено");
        }

        return stageInvitation;
    }

}
