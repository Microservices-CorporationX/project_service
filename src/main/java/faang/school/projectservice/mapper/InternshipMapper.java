package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface InternshipMapper {

    @Mapping(source = "mentorId.userId", target = "mentorId")
    @Mapping(source = "project.id", target = "ownedProjectId")
    @Mapping(source = "interns", target = "internIds", qualifiedByName = "mapInternIds")
    @Mapping(target = "role", ignore = true)
    InternshipDto toDto(Internship internship);

    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "interns", ignore = true)
    Internship toEntity(InternshipDto internshipDto);

    @Named("mapInternIds")
    default List<Long> mapInternIds(List<TeamMember> interns) {
        return interns.stream().map(TeamMember::getId).toList();
    }
}
