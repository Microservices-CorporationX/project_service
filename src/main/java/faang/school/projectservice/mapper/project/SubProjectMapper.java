package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {

    Project toProject(CreateSubProjectDto subProjectDto);

    CreateSubProjectDto toSubProjectDto(Project project);


}
