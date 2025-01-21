package faang.school.projectservice.mapper.stageinvitation;

import faang.school.projectservice.dto.stageinvitation.ChangeStatusDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChangeStatusMapper {
    @Mapping(target = "invited", ignore = true)
    StageInvitation toEntity(ChangeStatusDto dto);

    @Mapping(source = "invited.id", target = "invitedId")
    ChangeStatusDto toDto(StageInvitation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invited", ignore = true)
    @Mapping(source = "status", target = "status")
    void update(@MappingTarget StageInvitation entity, ChangeStatusDto dto);
}
