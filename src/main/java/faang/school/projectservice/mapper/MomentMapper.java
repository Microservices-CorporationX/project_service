package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MomentMapper {
    @Mapping(source = "date", target = "date", dateFormat = "dd/MM/yyyy HH:mm:ss")
    Moment toMomentEntity(MomentDto momentDto);

    MomentDto toMomentDto (Moment moment);

    List<Moment> toMomentEntities(List<MomentDto> momentDtos);

    List<MomentDto> toMomentDtos(List<Moment> moments);


}
