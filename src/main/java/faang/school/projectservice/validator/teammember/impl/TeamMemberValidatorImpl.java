package faang.school.projectservice.validator.teammember.impl;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.validator.teammember.TeamMemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamMemberValidatorImpl implements TeamMemberValidator {
    private final TeamMemberJpaRepository teamMemberJpaRepository;

    @Override
    public void validateTeamMemberExistence(long id) {
        boolean exists = teamMemberJpaRepository.existsById(id);
        if (!exists) {
            var message = String.format("a team member with %d does not exist", id);

            throw new DataValidationException(message);
        }
    }
}
