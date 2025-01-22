package faang.school.projectservice.service;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;

    public List<Moment> getMomentsByIds(List<Long> momentIds) {
        return momentRepository.findAllById(momentIds);
    }
}
