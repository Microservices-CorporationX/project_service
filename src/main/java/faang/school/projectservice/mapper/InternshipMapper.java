package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "mentorId", source = "entity", qualifiedByName = "getTeamMemberId")
    @Mapping(target = "projectId", source = "entity", qualifiedByName = "getProjectId")
    @Mapping(target = "internIds", source = "entity", qualifiedByName = "getTeamMembersIds")
    InternshipDto toDto(Internship entity);

    @Mapping(target = "mentorId", source = "dto", qualifiedByName = "getTeamMember")
    @Mapping(target = "project", source = "dto", qualifiedByName = "getProject")
    @Mapping(target = "interns", source = "dto", qualifiedByName = "getTeamMembersList")
    Internship toEntity(InternshipDto dto,
                        @Context TeamMemberService teamMemberService,
                        @Context ProjectService projectService);

    List<Internship> toEntities(List<InternshipDto> dtos,
                                @Context TeamMemberService teamMemberService,
                                @Context ProjectService projectService
    );

    List<InternshipDto> toDtos(List<Internship> entities);

    @Named("getTeamMember")
    default TeamMember getTeamMember(InternshipDto dto, @Context TeamMemberService teamMemberService) {
        return teamMemberService.getTeamMemberById(dto.mentorId());
    }

    @Named("getTeamMemberId")
    default Long getTeamMemberId(Internship entity) {
        return entity.getMentorId().getId();
    }

    @Named("getProject")
    default Project getProject(InternshipDto dto, @Context ProjectService projectService) {
        return projectService.getProjectById(dto.projectId());
    }

    @Named("getProjectId")
    default Long getProjectId(Internship entity) {
        return entity.getProject().getId();
    }

    @Named("getTeamMembersList")
    default List<TeamMember> getTeamMembersList(InternshipDto dto, @Context TeamMemberService teamMemberService) {
        return teamMemberService.getAllTeamMembersByIds(dto.internIds());
    }

    @Named("getTeamMembersIds")
    default List<Long> getTeamMembersIds(Internship entity) {
        return entity.getInterns().stream()
                .map(TeamMember::getId)
                .toList();
    }
}
