package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.donate.DonationCreateDto;
import faang.school.projectservice.dto.donate.DonationDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DonationMapper {

    @Mapping(target = "campaign.id", source = "campaignId")
    Donation toEntity(DonationCreateDto dto);

    @Mapping(target = "campaignId", source = "campaign.id")
    DonationDto toDto(Donation donation);
}
