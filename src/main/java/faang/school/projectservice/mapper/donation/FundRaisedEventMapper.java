package faang.school.projectservice.mapper.donation;

import faang.school.projectservice.dto.event.FundRaisedEvent;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FundRaisedEventMapper {
    @Mapping(target = "projectId", source = "campaign.project.id")
    @Mapping(target = "donationTime", expression = "java(java.time.LocalDateTime.now())")
    FundRaisedEvent donationToFundRaiseEvent(Donation donation);
}
