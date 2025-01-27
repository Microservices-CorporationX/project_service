package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.utils.Constants;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static java.time.LocalDateTime.parse;

@Slf4j
@Component
@RequiredArgsConstructor
public class MomentServiceValidator {

    private static final String DATE_FORMAT = Constants.DATE_FORMAT;

    private final ProjectService projectService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.ENGLISH);

    void validateMomentIdNotNull(MomentRequestDto momentRequestDto) {
        if (momentRequestDto.id() == null) {
            log.error("Unable update moment, because it's Id is null {}", momentRequestDto);
            throw new IllegalArgumentException("Unable update moment, because it's Id is null");
        }
    }

    void validateMoment(MomentRequestDto momentRequestDto) {
        validateMomentName(momentRequestDto);
        validateMomentDate(momentRequestDto);
        validateMomentAddedProjectIds(momentRequestDto);
    }

    private void validateMomentName(MomentRequestDto momentRequestDto) {
        if (StringUtils.isBlank(momentRequestDto.name())) {
            log.error("Moment cannot be with empty name!");
            throw new IllegalArgumentException("Moment cannot be with empty name!");
        }
    }

    private void validateMomentDate(MomentRequestDto momentRequestDto) {
        try {
            LocalDateTime date = parse(momentRequestDto.date(), formatter);
        } catch (Exception e) {
            log.error("Error converting string to date {} using format {}", momentRequestDto.date(), DATE_FORMAT);
            throw new IllegalArgumentException("Error converting date "
                    + momentRequestDto.date() + " using format " + DATE_FORMAT);
        }
    }

    private void validateMomentAddedProjectIds(MomentRequestDto momentRequestDto) {
        List<Long> projectIds = momentRequestDto.projectToAddIds();
        if (projectIds != null && !projectIds.isEmpty()) {
            List<ProjectResponseDto> associatedProjects = projectService.getProjectsByIds(projectIds);
            int activeProjectsListSize = associatedProjects.stream()
                    .filter(project -> ProjectStatus.IN_PROGRESS.toString().equals(project.status()))
                    .toList()
                    .size();
            if (activeProjectsListSize == 0) {
                log.error("Moment {} must have at least one active project!", momentRequestDto);
                throw new IllegalArgumentException("Moment must have at least one active project!");
            }
        }
    }
}
