package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    // константы ошибок
    // ...

    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public MomentDto createMoment(MomentDto momentDto) {

    }

    @Transactional
    public MomentDto updateMoment(MomentDto momentDto) {

    }

    @Transactional
    public List<MomentDto> getProjectMoments(Long projectId, MomentFilterDto momentFilterDto) {

    }

    @Transactional
    public List<MomentDto> getAllMoments() {

    }

    @Transactional
    public MomentDto getMomentById(Long momentId) {

    }
}
