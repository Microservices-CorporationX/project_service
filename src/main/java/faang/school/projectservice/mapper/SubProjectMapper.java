package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "parentProjectId", target = "parentProject.id")
    Project toEntity(SubProjectDto subProjectDto);

    List<SubProjectDto> toDto(List<Project> list);
}