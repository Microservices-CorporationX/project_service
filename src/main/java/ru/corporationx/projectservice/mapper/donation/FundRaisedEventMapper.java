package ru.corporationx.projectservice.mapper.donation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.corporationx.projectservice.model.dto.event.FundRaisedEvent;
import ru.corporationx.projectservice.model.entity.Donation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FundRaisedEventMapper {
    @Mapping(target = "projectId", source = "campaign.project.id")
    @Mapping(target = "donationTime", expression = "java(java.time.LocalDateTime.now())")
    FundRaisedEvent donationToFundRaiseEvent(Donation donation);
}
