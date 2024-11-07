package faang.school.projectservice.filter.campaign;

import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Campaign;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class CampaignCreatedAtFilter implements Filter<CampaignFilterDto, Campaign> {

    @Override
    public boolean isApplicable(CampaignFilterDto filterDto) {
        return filterDto.getCreatedAt() != null;
    }

    @Override
    public Stream<Campaign> applyFilter(Stream<Campaign> campaigns, CampaignFilterDto filterDto) {
        return campaigns.filter(campaign -> campaign.getCreatedAt().equals(filterDto.getCreatedAt()));
    }
}
