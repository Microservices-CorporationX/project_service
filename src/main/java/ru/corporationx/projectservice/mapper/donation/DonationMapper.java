package ru.corporationx.projectservice.mapper.donation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.corporationx.projectservice.model.dto.donation.DonationDto;
import ru.corporationx.projectservice.model.entity.Donation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonationMapper {
    Donation toEntity(DonationDto donationDto);

    @Mapping(target = "campaignId", source = "campaign.id")
    DonationDto toDto(Donation donation);
}
