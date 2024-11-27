package faang.school.projectservice.mapper.project.meet;

import faang.school.projectservice.dto.project.meet.MeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MeetDto toDto(Meet meet);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Meet toEntity(MeetDto meetDto);

    @Mapping(target = "creatorId", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(MeetDto meetDto,@MappingTarget Meet meet);
}
