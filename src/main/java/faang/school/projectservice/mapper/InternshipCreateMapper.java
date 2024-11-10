package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.internShip.InternshipCreatedDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipCreateMapper {
    @Mapping(source = "projectId", target = "project")
    @Mapping(source = "mentorId", target = "mentorId")
    Internship toEntity(InternshipCreatedDto dto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    InternshipCreatedDto toDto(Internship entity);

    default Project mapProject(Long projectId) {
        if (projectId == null) {
            return null;
        }
        Project project = new Project();
        project.setId(projectId);
        return project;
    }

    default TeamMember mapMentor(Long mentorId) {
        if (mentorId == null) {
            return null;
        }
        TeamMember mentor = new TeamMember();
        mentor.setId(mentorId);
        return mentor;
    }
}
