package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public Project getProjectById(long id) {
        log.debug("Search by id: %d".formatted(id));
        return projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No project found with id: %d".formatted(id)));
    }
}
