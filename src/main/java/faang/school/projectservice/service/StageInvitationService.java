package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.AcceptStageInvitation;
import faang.school.projectservice.dto.client.RejectStageInvitation;
import faang.school.projectservice.dto.client.StageInvitationDto;
import faang.school.projectservice.dto.client.StageInvitationFilters;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageService stageService;
    private final StageInvitationMapper stageInvitationMapper;
    private final TeamMemberService teamMemberService;

    private final List<Filter<StageInvitation, StageInvitationFilters>> stageInvitationFilters;

    public StageInvitationDto sendStageInvitation(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = StageInvitation
                .builder()
                .stage(stageService.getById(stageInvitationDto.getStageId()))
                .author(teamMemberService.getTeamMemberByUserId(stageInvitationDto.getAuthorId()))
                .invited(teamMemberService.getTeamMemberByUserId(stageInvitationDto.getInvitedId()))
                .status(StageInvitationStatus.PENDING)
                .build();

        stageInvitationRepository.save(stageInvitation);

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto acceptStageInvitation(AcceptStageInvitation stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(stageInvitationDto.getId());
        stageInvitation.setStatus(StageInvitationStatus.ACCEPTED);
        stageInvitationRepository.save(stageInvitation);
        stageService.setExecutor(stageInvitation.getStage().getStageId(), stageInvitation.getInvited().getId());

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public StageInvitationDto rejectStageInvitation(RejectStageInvitation rejectStageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationRepository.findById(rejectStageInvitationDto.getId());
        stageInvitation.setStatus(StageInvitationStatus.REJECTED);
        stageInvitation.setDescription(rejectStageInvitationDto.getDescription());

        stageInvitationRepository.save(stageInvitation);

        return stageInvitationMapper.toDto(stageInvitation);
    }

    public List<StageInvitationDto> filters(StageInvitationFilters filters) {
        Stream<StageInvitation> stageInvitation = stageInvitationRepository.findAll().stream();

        Stream<StageInvitation> filteredGoals = stageInvitationFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(
                        stageInvitation,
                        (currentStream, filter) -> filter.apply(currentStream, filters),
                        (s1, s2) -> s1
                );

        return filteredGoals.map(stageInvitationMapper::toDto).toList();
    }
}
