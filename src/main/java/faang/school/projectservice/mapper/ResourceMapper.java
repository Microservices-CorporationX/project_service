package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "project", ignore = true)
    Resource toEntity(ResourceDto resourceDto);

    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "updatedBy", source = "updatedBy.id")
    @Mapping(target = "project", source = "project.id")
    ResourceDto toDto(Resource resource);

    List<Resource> toEntityList(List<ResourceDto> resourceDtos);

    List<ResourceDto> toDtoList(List<Resource> resources);
}
