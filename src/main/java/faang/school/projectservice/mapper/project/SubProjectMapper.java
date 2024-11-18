package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {


    Project toProject(CreateSubProjectDto subProjectDto);


    CreateSubProjectDto toSubProjectDto(Project project);


}
