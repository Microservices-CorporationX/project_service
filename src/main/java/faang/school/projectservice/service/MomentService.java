package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.filters.MomentFilter;

import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;

    public MomentDto create(MomentDto momentDto) {
        if (isExists(momentDto)) {
            throw new ValidationException("This moment already exists");
        }
        Moment moment = momentMapper.toEntity(momentDto);
        Moment savedMoment = momentRepository.save(moment);
        return (momentMapper.toDto(savedMoment));
    }

    public MomentDto update(MomentDto momentDto) {

        Moment moment = momentMapper.toEntity(momentDto);
        Moment updatedMoment = momentRepository.save(moment);
        return (momentMapper.toDto(updatedMoment));
    }

    public List<MomentDto> getMomentsByFilter(MomentFilterDto filterDto) {
        List<Moment> allMoments = momentRepository.findAll();
        Stream<Moment> momentStream = allMoments.stream();
        return momentFilters.stream().filter(momentFilter -> momentFilter.isApplicable(filterDto))
                .reduce(momentStream, (stream, eventFilter) -> eventFilter.apply(stream, filterDto), (s1, s2) -> s2)
                .map(momentMapper::toDto).toList();
    }

    public List<MomentDto> getAllMoments() {
        List<Moment> receivedMoments = momentRepository.findAll();
        return momentMapper.toDtoList(receivedMoments);
    }

    public MomentDto getMomentById(long id) {
        Optional<Moment> receivedMoment = momentRepository.findById(id);
        if (receivedMoment.isPresent()) {
            Moment foundMoment = receivedMoment.get();
            return momentMapper.toDto(foundMoment);
        }
        throw new ValidationException("There is no moment with given Id");
    }

    private boolean isExists(MomentDto momentDto) {
        List<Moment> presentMomentsList = momentRepository.findAll();
        List<MomentDto> presentMomentDtoList = momentMapper.toDtoList(presentMomentsList);
        return presentMomentDtoList.contains(momentDto);

    }
}
