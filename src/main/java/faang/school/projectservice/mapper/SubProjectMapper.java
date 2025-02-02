package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.subprojectdto.SubProjectCreateDto;
import faang.school.projectservice.dto.client.subprojectdto.ProjectReadDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {

    @Mapping(source = "parentProjectId", target = "parentProject.id")
    Project mapToEntity(SubProjectCreateDto createSubProjectDto);

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    ProjectReadDto mapToProjectDto(Project project);

}
