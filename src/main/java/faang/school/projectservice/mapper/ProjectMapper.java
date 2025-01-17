package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.CreateProjectRequestDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "ownerId", ignore = true),
            @Mapping(target = "storageSize", ignore = true),
            @Mapping(target = "maxStorageSize", ignore = true),
            @Mapping(target = "children", ignore = true),
            @Mapping(target = "tasks", ignore = true),
            @Mapping(target = "resources", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "coverImageId", ignore = true),
            @Mapping(target = "teams", ignore = true),
            @Mapping(target = "schedule", ignore = true),
            @Mapping(target = "stages", ignore = true),
            @Mapping(target = "vacancies", ignore = true),
            @Mapping(target = "moments", ignore = true),
            @Mapping(target = "meets", ignore = true),
            @Mapping(target = "presentationFileKey", ignore = true),
            @Mapping(target = "presentationGeneratedAt", ignore = true),
            @Mapping(target = "galleryFileKeys", ignore = true),
            @Mapping(target = "parentProject", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    Project toEntity(CreateProjectRequestDto createProjectRequestDto);

    @Mapping(target = "updatedAt", source = "updatedAt")
    ProjectResponseDto toResponseDto(Project project);
}