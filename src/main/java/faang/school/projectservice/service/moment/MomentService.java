package faang.school.projectservice.service.moment;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MomentService {
    private final MomentRepository momentRepository;

    public Moment create() {
        return new Moment();
    }

    public List<Moment> getAllByProjectId(Long projectId) {
        return momentRepository.findAllByProjectId(projectId);
    }
}