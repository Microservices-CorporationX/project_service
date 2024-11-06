package faang.school.projectservice.dto.campaign;

import faang.school.projectservice.model.CampaignStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CampaignFilterDto {
    private Long createdById;
    private String titlePattern;
    private BigDecimal minGoal;
    private BigDecimal maxGoal;
    private CampaignStatus status;
    private LocalDate createdAfter;
}
