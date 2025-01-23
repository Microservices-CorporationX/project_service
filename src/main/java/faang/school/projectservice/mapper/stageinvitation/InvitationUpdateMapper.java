package faang.school.projectservice.mapper.stageinvitation;

import faang.school.projectservice.dto.stageinvitation.StageInvitationUpdateDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvitationUpdateMapper {
    @Mapping(source = "stage.stageId", target = "stageId")
    StageInvitationUpdateDto toDto(StageInvitation entity);

    @Mapping(target = "stage", ignore = true)
    StageInvitation toEntity(StageInvitationUpdateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stage", ignore = true)
    void update(@MappingTarget StageInvitation entity, StageInvitationUpdateDto dto);
}
