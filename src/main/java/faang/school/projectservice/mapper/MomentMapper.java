package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentCreateRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentUpdateRequestDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    //@Mapping(source = "date", target = "date", dateFormat = "dd/MM/yyyy HH:mm:ss")
    Moment toMomentEntity(MomentResponseDto momentResponseDto);
    //@Mapping(source = "date", target = "date", dateFormat = "dd/MM/yyyy HH:mm:ss")
    Moment toMomentEntity(MomentUpdateRequestDto momentUpdateResponseDto);
    //@Mapping(source = "date", target = "date", dateFormat = "dd/MM/yyyy HH:mm:ss")
    Moment toMomentEntity(MomentCreateRequestDto momentCreateResponseDto);
    MomentResponseDto toMomentResponseDto (Moment moment);
    MomentUpdateRequestDto toMomentRequestDto (Moment moment);
    MomentCreateRequestDto toMomentCreateRequestDto (Moment moment);
    List<Moment> toMomentEntities(List<MomentResponseDto> momentResponseDtos);
    List<MomentResponseDto> toMomentResponseDtos(List<Moment> moments);
    List<MomentUpdateRequestDto> toMomentRequestDtos(List<Moment> moments);
    List<MomentCreateRequestDto> toMomentCreateRequestDtos(List<Moment> moments);
}
