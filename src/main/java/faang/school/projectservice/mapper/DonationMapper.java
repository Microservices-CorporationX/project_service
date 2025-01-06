package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationResponseDto;
import faang.school.projectservice.dto.donation.FilterDonationDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DonationMapper {

    @Mapping(target = "campaignId", source = "campaign.id")
    DonationResponseDto toDonationResponseDto(Donation donation);

    Donation toEntity(DonationDto donationDto);

    Donation toEntity(FilterDonationDto filterDonationDto);

    List<DonationResponseDto> toDonationsResponseDto(List<Donation> donations);

    PaymentRequest toPaymentRequest(Donation donation);
}
