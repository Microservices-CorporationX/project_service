package faang.school.projectservice.validator;

import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CampaignValidator {

    public void validateAuthorRole(Project project, long authorId) {
        log.debug("Validating that author under id: {} is owner or manager of the project: {}", authorId, project);
        if (project.isUserNotOwner(authorId) && project.isUserNotManager(authorId)) {
            throw new DataValidationException(
                    "Access denied. In order to create/update campaign you must be either owner or a manager"
            );
        }
    }

    public void validateCampaignIsNotDeleted(Campaign campaign) {
        log.debug("Validating that campaign: {} is not deleted", campaign);
        if (campaign.isDeleted()) {
            throw new DataValidationException("Campaign deleted. Cannot modify");
        }
    }
}
