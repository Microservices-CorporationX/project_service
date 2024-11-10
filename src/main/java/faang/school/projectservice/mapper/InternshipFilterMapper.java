package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.internShip.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipFilterMapper {
    InternshipFilterDto toDto(Internship internship);
    InternshipFilterDto toDto(TeamMember teamMember);
}
