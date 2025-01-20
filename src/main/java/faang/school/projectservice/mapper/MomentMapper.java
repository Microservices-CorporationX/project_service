package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MomentMapper {
    @Mapping(source = "date", target = "date", dateFormat = "dd/MM/yyyy HH:mm:ss")
    Moment toMomentEntity(MomentResponseDto momentResponseDto);

    @Mapping(source = "date", target = "date", dateFormat = "dd/MM/yyyy HH:mm:ss")
    Moment toMomentEntity(MomentRequestDto momentResponseDto);

    MomentResponseDto toMomentResponseDto (Moment moment);

    MomentRequestDto toMomentRequestDto (Moment moment);

    List<Moment> toMomentEntities(List<MomentResponseDto> momentResponseDtos);

    //List<Moment> toMomentEntities(List<MomentRequestDto> momentRequestDtos);

    List<MomentResponseDto> toMomentResponseDtos(List<Moment> moments);

    List<MomentRequestDto> toMomentRequestDtos(List<Moment> moments);


}
