package faang.school.projectservice.mapper.stageinvitation;

import faang.school.projectservice.dto.stageinvitation.RejectInvitationDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ChangeStatusMapper.class)
public interface RejectInvitationMapper {
    @Mapping(source = "statusDto", target = ".")
    StageInvitation toEntity(RejectInvitationDto dto);

    @Mapping(source = "entity", target = "statusDto")
    RejectInvitationDto toDto(StageInvitation entity);
}
