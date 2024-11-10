package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.internShip.InternShipCreatedDto;
import faang.school.projectservice.dto.client.internShip.InternShipUpdatedDto;
import faang.school.projectservice.handler.InternshipCompletionHandler;
import faang.school.projectservice.mapper.InternShipCreateMapper;
import faang.school.projectservice.mapper.InternShipUpdateMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validator.InternshipDurationValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final ProjectService projectService;
    private final InternshipRepository internshipRepository;
    private final InternShipCreateMapper internShipCreateMapper;
    private final InternShipUpdateMapper internShipUpdateMapper;
    private final InternshipDurationValidator internShipDurationValidator;
    private final InternshipCompletionHandler completionHandler;

    public InternShipCreatedDto create(InternShipCreatedDto internShipCreatedDto) {
        internShipDurationValidator.durationValidate(internShipCreatedDto);
        projectService.getProjectTeamMembersIds(internShipCreatedDto);
        return internShipCreateMapper.toDto(internshipRepository.save(internShipCreateMapper.toEntity(internShipCreatedDto)));
    }

    public InternShipUpdatedDto updatedDto(InternShipUpdatedDto internShipUpdatedDto) {
        Internship internship = internshipRepository.findById(internShipUpdatedDto.getId())
                .orElseThrow(EntityNotFoundException::new);

        completionHandler.internsToDismissal(internShipUpdatedDto.getInternToDismissal());

        if (internShipUpdatedDto.getStatus() == InternshipStatus.COMPLETED) {
            completionHandler.handleInternsCompletion(internship);
        }

        Internship savedInternship = internshipRepository.save(internShipUpdateMapper.toEntity(internShipUpdatedDto));
        return internShipUpdateMapper.toDto(savedInternship);
    }
}
