package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.InternshipDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InternshipMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "mentorId", source = "mentorId.id")
    @Mapping(target = "internIds", expression = "java(internship.getInterns().stream().map(i -> i.getId()).toList())")
    InternshipDto toDto(Internship internship);

    @Mapping(target = "project.id", source = "projectId")
    @Mapping(target = "mentorId.id", source = "mentorId")
    @Mapping(target = "interns", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    Internship toEntity(InternshipDto internshipDto);
}