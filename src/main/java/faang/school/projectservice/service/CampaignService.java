package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.FilterCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.validator.CampaignValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignValidator campaignValidator;
    private final CampaignMapper campaignMapper;
    private final ProjectService projectService;

    @Transactional
    public CampaignDto createCampaign(CreateCampaignDto createCampaignDto) {
        log.info("Trying to create campaign: {}", createCampaignDto);
        Project project = projectService.findProjectById(createCampaignDto.projectId());
        campaignValidator.validateAuthorRole(project, createCampaignDto.createdBy());
        Campaign campaign = campaignMapper.toEntity(createCampaignDto);
        campaign.setProject(project);
        campaignRepository.save(campaign);
        return campaignMapper.toCampaignDto(campaign);
    }

    @Transactional
    public CampaignDto updateCampaign(UpdateCampaignDto updateCampaignDto, long campaignId) {
        log.info("Trying to update campaign under id: {} with the following parameters: {}",
                campaignId, updateCampaignDto);
        Campaign campaign = findCampaignById(campaignId);
        campaignValidator.validateCampaignIsNotDeleted(campaign);
        campaignValidator.validateAuthorRole(campaign.getProject(), updateCampaignDto.updatedBy());
        campaignMapper.update(campaign, updateCampaignDto);
        return campaignMapper.toCampaignDto(campaign);
    }

    @Transactional
    public void softDeleteCampaign(long campaignId) {
        log.info("Trying to soft delete campaign under id: {}", campaignId);
        campaignRepository.markAsDeleted(campaignId);
    }

    @Transactional
    public List<CampaignDto> getFilteredCampaigns(FilterCampaignDto filterCampaignDto) {
        log.info("Trying to get campaigns with the following filters: {}", filterCampaignDto);
        List<Campaign> campaigns = campaignRepository.getFilteredCampaigns(filterCampaignDto);
        return campaignMapper.toCampaignDto(campaigns);
    }

    private Campaign findCampaignById(long campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Campaign under id %d does not exist", campaignId)
                ));
    }
}
