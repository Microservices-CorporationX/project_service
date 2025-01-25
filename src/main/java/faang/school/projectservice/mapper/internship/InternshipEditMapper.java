package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipEditDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipEditMapper extends InternshipMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "interns", target = "internsIds", qualifiedByName = "mapToIds")
    InternshipEditDto toDto(Internship internship);

    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "internsIds", ignore = true)
    @Mapping(target = "targetRole", ignore = true)
    Internship toEntity(InternshipEditDto internshipDto);
}
