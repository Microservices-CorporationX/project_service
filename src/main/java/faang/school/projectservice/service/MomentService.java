package faang.school.projectservice.service;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;

    @Transactional
    public void createMoment(Moment moment) {
        momentRepository.save(moment);
    }
}
