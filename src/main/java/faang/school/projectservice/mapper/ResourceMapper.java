package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.resource.ResourceRequestDto;
import faang.school.projectservice.dto.resource.ResourceResponseDto;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {
    Resource toResourceEntity(ResourceRequestDto resourceRequestDto);
    Resource toResourceEntity(ResourceResponseDto resourceResponseDto);
    ResourceRequestDto toResourceRequestDto(Resource resource);
    ResourceResponseDto toResourceResponseDto(Resource resource);

}
