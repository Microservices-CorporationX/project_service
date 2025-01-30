package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.model.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface VacancyMapper {
    @Mappings({
            @Mapping(target = "project", source = "projectId", qualifiedByName = "map"),
            @Mapping(target = "position", source = "positionId", qualifiedByName = "map1"),
            @Mapping(target = "curatorRole", source = "curatorRoleId", qualifiedByName = "map1"),
            @Mapping(target = "status", source = "statusId", qualifiedByName = "map2"),
            @Mapping(target = "candidates", source = "candidatesIds", qualifiedByName = "map3"),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "workSchedule", ignore = true),
    })
    Vacancy toEntity(VacancyDto dto);

    @Mappings({
            @Mapping(target = "projectId", source = "project", qualifiedByName = "map4"),
            @Mapping(target = "positionId", source = "position", qualifiedByName = "map5"),
            @Mapping(target = "curatorRoleId", source = "curatorRole", qualifiedByName = "map5"),
            @Mapping(target = "statusId", source = "status", qualifiedByName = "map6"),
            @Mapping(target = "candidatesIds", source = "candidates", qualifiedByName = "map7"),
    })
    VacancyDto toDto(Vacancy entity);

    @Mappings({
            @Mapping(target = "project", source = "projectId", qualifiedByName = "map"),
            @Mapping(target = "position", source = "positionId", qualifiedByName = "map1"),
            @Mapping(target = "curatorRole", source = "curatorRoleId", qualifiedByName = "map1"),
            @Mapping(target = "status", source = "statusId", qualifiedByName = "map2"),
            @Mapping(target = "candidates", source = "candidatesIds", qualifiedByName = "map3"),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "workSchedule", ignore = true),
    })
    Vacancy update(@MappingTarget Vacancy vacancy, VacancyDto vacancyDto);

    @Named("map")
    default Project map(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return Project.builder().id(projectId).build();
    }

    @Named("map1")
    default TeamRole map1(Integer someInt) {
        if (someInt == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return TeamRole.getAll().get(someInt);
    }

    @Named("map2")
    default VacancyStatus map2(Integer someInt) {
        if (someInt == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return VacancyStatus.getAll().get(someInt);
    }

    @Named("map3")
    default List<Candidate> map3(List<Long> candidates) {
        if (candidates == null) {
            throw new IllegalArgumentException("Candidates cannot be null");
        }
        return candidates.stream()
                .map(candidateId -> {
                    Candidate candidate = new Candidate();
                    candidate.setId(candidateId); // Нестатический вызов метода setId
                    return candidate;
                })
                .toList();
    }
    @Named("map4")
    default Long map4(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return project.getId();
    }
    @Named("map5")
    default Integer map5(TeamRole someTeamRole) {
        if (someTeamRole == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return someTeamRole.ordinal();
    }
    @Named("map6")
    default Integer map6(VacancyStatus someStatus) {
        if (someStatus == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return someStatus.ordinal();
    }
    @Named("map7")
    default List<Long> map7(List<Candidate> candidates) {
        if (candidates == null) {
            throw new IllegalArgumentException("Candidates cannot be null");
        }
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }


}
