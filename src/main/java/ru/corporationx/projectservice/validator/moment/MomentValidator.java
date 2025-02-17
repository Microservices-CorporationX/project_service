package ru.corporationx.projectservice.validator.moment;

import ru.corporationx.projectservice.exception.DataValidationException;
import ru.corporationx.projectservice.model.dto.moment.MomentDto;
import ru.corporationx.projectservice.model.entity.Moment;
import ru.corporationx.projectservice.model.entity.ProjectStatus;
import ru.corporationx.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MomentValidator {

    private final MomentRepository momentRepository;

    public void validateUniqueMoment(MomentDto momentDto) {
        if (momentRepository.findById(momentDto.getId()).isPresent()) {
            log.warn("Moment with id {} already exists", momentDto.getId());
            throw new DataValidationException("Moment with id " + momentDto.getId() + " already exists");
        }
    }

    public void validateActiveMoment(Moment moment) {
        if (moment.getProjects() != null
                && moment.getProjects().stream().anyMatch(  project -> project.getStatus().equals(ProjectStatus.COMPLETED)
                || project.getStatus().equals(ProjectStatus.ON_HOLD)
                || project.getStatus().equals(ProjectStatus.CANCELLED))){
            log.warn("Moment id: {} cannot be created for inactive projects", moment.getId());
            throw new DataValidationException("Moment id: " + moment.getId() + " cannot be created for inactive projects");
        }
    }

    public void validateMomentExists(Long id) {
        if (momentRepository.findById(id).isEmpty()) {
            log.warn("Moment with id {} does not exist", id);
            throw new DataValidationException("Moment with id " + id + " does not exist");
        }
    }
}
