package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.model.project.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {
    ResponseProjectDto toResponseDto(Project project);

    Project toEntity(ResponseProjectDto responseProjectDto);

    List<ResponseProjectDto> toResponseDto(List<Project> projects);

    List<Project> toEntity(List<ResponseProjectDto> responseProjectDtos);
}
