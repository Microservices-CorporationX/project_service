package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.moment.CreateMomentRequest;
import faang.school.projectservice.dto.moment.CreateMomentResponse;
import faang.school.projectservice.dto.moment.GetMomentResponse;
import faang.school.projectservice.dto.moment.UpdateMomentRequest;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MomentMapper {

    Moment toEntity(CreateMomentRequest createMomentRequest, List<Project> projects,
                    List<Resource> resources, Long createdBy);

    CreateMomentResponse toCreateMomentResponse(Moment moment);

    GetMomentResponse toGetMomentResponse(Moment moment);

    List<GetMomentResponse> toGetMomentResponseList(List<Moment> moments);

    @Mapping(target = "userIds", ignore = true)
    @Mapping(target = "projects", ignore = true)
    void updateMoment(@MappingTarget Moment moment, UpdateMomentRequest updateMomentRequest, Long updatedBy);
}
