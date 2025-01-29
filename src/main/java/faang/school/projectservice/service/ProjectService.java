package faang.school.projectservice.service;

import faang.school.projectservice.repository.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    private ProjectRepository projectRepository;

    public void getTest() {
        projectRepository.findById(2L);
    }
}
