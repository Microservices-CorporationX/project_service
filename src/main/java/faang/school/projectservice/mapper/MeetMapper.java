package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {

    @Mapping(source = "project.id", target = "projectId")
    MeetDto toDto(Meet meet);

    @Mapping(target = "project", ignore = true)
    Meet toMeet(MeetDto meetDto);

    List<MeetDto> toMeetDtoList(List<Meet> meets);

    List<Meet> toMeetList(List<MeetDto> meetDtos);
}
