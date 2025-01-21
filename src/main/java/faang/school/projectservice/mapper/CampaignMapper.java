package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.model.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampaignMapper {

    @Mapping(target = "projectId", source = "project.id")
    CampaignDto toCampaignDto(Campaign campaign);

    @Mapping(target = "project", ignore = true)
    Campaign toEntity(CreateCampaignDto createCampaignDto);

    List<CampaignDto> toCampaignDto(List<Campaign> campaign);

    void update(@MappingTarget Campaign campaign, UpdateCampaignDto updateCampaignDto);
}
