package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "mentorId", target = "mentorId.id")
    @Mapping(source = "internshipStatus", target = "status")
    @Mapping(target = "interns", ignore = true)
    Internship toEntity(InternshipDto internshipDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "status", target = "internshipStatus")
    @Mapping(source = "interns", target = "internIds", qualifiedByName = "mapToInternIds")
    InternshipDto toDto(Internship internship);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "status", target = "internshipStatus")
    @Mapping(source = "interns", target = "internIds", qualifiedByName = "mapToInternIds")
    List<InternshipDto> mapToDtoList(List<Internship> internships);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "mentorId", target = "mentorId.id")
    @Mapping(target = "id", ignore = true)
    Internship update(InternshipDto internshipDto, @MappingTarget Internship internship);

    @Named("mapToInternIds")
    default List<Long> map(List<TeamMember> interns) {
        return interns.stream().map(TeamMember::getId).toList();
    }
}
