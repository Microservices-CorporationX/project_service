package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipEditDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipEditMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "interns", target = "internsIds", qualifiedByName = "mapToIds")
    InternshipEditDto toDto(Internship internship);

    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "internsIds", ignore = true)
    @Mapping(target = "targetRole", ignore = true)
    Internship toEntity(InternshipEditDto internshipDto);

    @Named("mapToIds")
    default List<Long> mapToIds(List<TeamMember> interns) {
        return interns.stream().map(TeamMember::getId).toList();
    }

//    @Named("mapToInterns")
//    default List<TeamMember> mapToInterns(List<Long> internsIds) {
//        return internsIds.stream().map()
//    }

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "mentorId", target = "mentorId.id")
    @Mapping(source = "internsIds", target = "interns")
    @Mapping(target = "targetRole", ignore = true)
    void update(@MappingTarget Internship entity, InternshipEditDto dto);
}
