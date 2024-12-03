package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    MomentDto toDto(Moment moment);
    Moment toEntity(MomentDto momentDto);

    List<MomentDto> toDtoList(List<Moment> momentList);

    List<Moment> toEntityList(List<MomentDto> momentDtoList);
}
