package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.internShip.InternshipGetByIdDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipGetByIdMapper {
    @Mapping(source = "mentorId.id", target = "mentorId")
    InternshipGetByIdDto toDto(Internship internship);
}
