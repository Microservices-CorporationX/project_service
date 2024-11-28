package faang.school.projectservice.mapper.resource;

import faang.school.projectservice.dto.resource.ResponseResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    ResponseResourceDto toDtoFromEntity(Resource resource);
}
