package faang.school.projectservice.controller;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.FilterCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.service.CampaignService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
public class CampaignV1Controller {

    private final CampaignService campaignService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignDto createCampaign(@RequestBody @Valid CreateCampaignDto createCampaignDto) {
        return campaignService.createCampaign(createCampaignDto);
    }

    @PutMapping("/{campaignId}")
    public CampaignDto updateCampaign(@RequestBody @Valid UpdateCampaignDto updateCampaignDto,
                                      @Positive @PathVariable long campaignId) {
        return campaignService.updateCampaign(updateCampaignDto, campaignId);
    }

    @DeleteMapping("/{campaignId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeleteCampaign(@PathVariable @Positive long campaignId) {
        campaignService.softDeleteCampaign(campaignId);
    }

    @PostMapping("/retrieve")
    public List<CampaignDto> getAllFilteredCampaigns(@RequestBody FilterCampaignDto filterCampaignDto) {
        return campaignService.getFilteredCampaigns(filterCampaignDto);
    }
}
