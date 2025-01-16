package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MomentMapper {
    Moment toMomentEntity(MomentDto momentDto);

    MomentDto toMomentDto (Moment moment);

    List<Moment> toMomentEntities(List<MomentDto> momentDtos);

    List<MomentDto> toMomentDtos(List<Moment> moments);


}
