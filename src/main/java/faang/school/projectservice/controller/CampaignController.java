package faang.school.projectservice.controller;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    @GetMapping("/{campaignId}")
    public CampaignDto getCampaign(@PathVariable Long campaignId) {
        return null;
    }

    @GetMapping("/{projectId}")
    public List<CampaignDto> getCampaignsForProject(@PathVariable Long projectId,
                                                    @RequestBody CampaignFilterDto filter) {
        return null;
    }

    @PostMapping
    public CampaignDto createCampaign(@RequestBody CampaignDto campaignDto) {
        return null;
    }

    @PutMapping("/{campaignId}")
    public CampaignDto updateCampaign(@PathVariable Long campaignId,
                                      @RequestBody CampaignDto campaignDto) {
        return null;
    }

}
