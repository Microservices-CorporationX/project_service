package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.internShip.InternshipGetAllDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipGetAllMapper {
    @Mapping(source = "mentorId.id", target = "mentorId")
    InternshipGetAllDto toDto(Internship internship);
}
