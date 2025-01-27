package faang.school.projectservice.service;


import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;

import java.util.List;

public interface MomentService {
    MomentResponseDto createMoment(MomentRequestDto momentRequestDto);
    MomentResponseDto updateMoment(MomentRequestDto momentRequestDto);
    List<MomentResponseDto> getMoments(MomentFilterDto filter);
    List<MomentResponseDto> getAllMoments();
    MomentResponseDto getMoment(Long momentId);
}
