package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.MomentFilter;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomentServiceImpl implements MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;


    @Override
    public MomentDto createMoment(MomentDto momentDto) {
        Moment moment = momentRepository.save(momentMapper.toMomentEntity(momentDto));
        MomentDto createdMomentDto = momentMapper.toMomentDto(moment);
        log.info("Created moment {}", createdMomentDto);
        return createdMomentDto;
    }

    @Override
    public MomentDto updateMoment(MomentDto momentDto) {
        if (momentDto.id() != null) {
            log.info("Updated moment : {}", momentDto);
            return createMoment(momentDto);
        }else
        {
            log.error("Unable update moment, because it's Id is null {}", momentDto);
            throw new IllegalArgumentException("Unable update moment, because it's Id is null");
        }
    }

    @Override
    public List<MomentDto> getMoments(MomentFilterDto filter) {
        List<Moment> moments = momentRepository.findAll();
        return momentMapper.toMomentDtos(moments);
    }

    @Override
    public List<MomentDto> getAllMoments() {
        List<Moment> moments = momentRepository.findAll();
        return momentMapper.toMomentDtos(moments);
    }

    @Override
    public MomentDto getMoment(Long id) {
        Moment moment = momentRepository.getReferenceById(id);
        return momentMapper.toMomentDto(moment);
    }
}
