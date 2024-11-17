package faang.school.projectservice.validator.teamMember;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.teammember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberValidatorTest {

    @InjectMocks
    private TeamMemberValidator teamMemberValidator;

    @Mock
    private TeamMemberService teamMemberService;

    private static final long ID = 1L;
    private TeamMember teamMember;

    @BeforeEach
    public void init() {
        teamMember = TeamMember.builder()
                .id(ID)
                .userId(ID)
                .build();
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Exception when the user is not a member of team")
        public void whenUserIsNotMemberOfTeamThenThrowException() {
            when(teamMemberService.findByUserIdAndProjectId(ID, ID)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> teamMemberValidator.validateUserHasStatusOwnerOrManagerInTeam(ID, ID));
        }

        @Test
        @DisplayName("Exception when user does not have status Owner or Manager in team")
        public void whenUserIsNotValidThenThrowException() {
            teamMember.setRoles(List.of(TeamRole.ANALYST));
            when(teamMemberService.findByUserIdAndProjectId(ID, ID)).thenReturn(Optional.of(teamMember));

            assertThrows(DataValidationException.class,
                    () -> teamMemberValidator.validateUserHasStatusOwnerOrManagerInTeam(ID, ID));
        }
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Success when user is team member and has status Owner")
        public void whenUserHasStatusOwnerThenSuccess() {
            teamMember.setRoles(List.of(TeamRole.OWNER));
            when(teamMemberService.findByUserIdAndProjectId(ID, ID)).thenReturn(Optional.of(teamMember));

            assertDoesNotThrow(() -> teamMemberValidator.validateUserHasStatusOwnerOrManagerInTeam(ID, ID));
        }

        @Test
        @DisplayName("Success when user is team member and has status Manager")
        public void whenUserHasStatusManagerThenSuccess() {
            teamMember.setRoles(List.of(TeamRole.MANAGER));
            when(teamMemberService.findByUserIdAndProjectId(ID, ID)).thenReturn(Optional.of(teamMember));

            assertDoesNotThrow(() -> teamMemberValidator.validateUserHasStatusOwnerOrManagerInTeam(ID, ID));
        }
    }
}