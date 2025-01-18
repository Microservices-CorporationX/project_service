package faang.school.projectservice.service.impl;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.MomentFilter;
import faang.school.projectservice.service.MomentService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.time.LocalDateTime.parse;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomentServiceImpl implements MomentService {
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.ENGLISH);

    @Override
    public MomentDto createMoment(MomentDto momentDto) {
        validateMoment(momentDto);
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
        } else {
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
        Optional<Moment> optionalMoment = momentRepository.findById(id);
        Moment moment = optionalMoment.orElseThrow();
        return momentMapper.toMomentDto(moment);
    }

    private void validateMoment(MomentDto momentDto) {
        if (StringUtils.isBlank(momentDto.name())) {
            log.error("Moment cannot be with empty name!");
            throw new IllegalArgumentException("Moment cannot be with empty name!");
        }

        try {
            LocalDateTime date = parse(momentDto.date(), formatter);
        } catch (Exception e) {
            log.error("Error converting date {} using format {}", momentDto.date(), DATE_FORMAT);
            throw new IllegalArgumentException("Error converting date "
                    + momentDto.date() + " using format " + DATE_FORMAT);
        }


        List<Long> projectIds = momentDto.projectIds();
        if (projectIds == null || projectIds.isEmpty()) {
            log.error("Moment cannot be without projects!");
            throw new IllegalArgumentException("Moment cannot be without projects!");
        }

        List<Long> teamMembersIds = momentDto.teamMembersIds();
    }
}
