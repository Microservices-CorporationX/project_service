package faang.school.projectservice.mapper.donation;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonationMapper {

    @Mapping(source = "campaign.id", target = "campaignId")
    DonationDto toDto(Donation donation);

    @Mapping(source = "campaignId", target = "campaign", qualifiedByName = "campaignIdToCampaign")
    Donation toEntity(DonationDto donationDto);

    List<DonationDto> toDto(List<Donation> donations);

    List<Donation> toEntity(List<DonationDto> donationDtos);

    @Named("campaignIdToCampaign")
    default Campaign campaignIdToCampaign(long campaignId) {
        return Campaign.builder()
                .id(campaignId)
                .build();
    }
}
