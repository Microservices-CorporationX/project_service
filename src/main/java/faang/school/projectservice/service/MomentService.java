package faang.school.projectservice.service;


import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;

import java.util.List;

public interface MomentService {

    MomentDto createMoment(MomentDto momentDto);

    MomentDto updateMoment(MomentDto momentDto);

    List<MomentDto> getMoments(MomentFilterDto filter);

    List<MomentDto> getAllMoments();

    MomentDto getMoment(Long momentId);
}
