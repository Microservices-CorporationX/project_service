package faang.school.projectservice.validator.resource;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResourceValidatorTest {
    @InjectMocks
    private ResourceValidator resourceValidator;

    @Test
    void testCheckProjectStorageSizeExceeded_Positive() {
        Project project = Project.builder()
                .maxStorageSize(BigInteger.valueOf(2_000_000L))
                .build();
        BigInteger newStorageSize = BigInteger.valueOf(1_001_000L);
        Assertions.assertDoesNotThrow(() -> resourceValidator.checkProjectStorageSizeExceeded(newStorageSize, project));
    }

    @Test
    void testCheckProjectStorageSizeExceeded_Negative() {
        Project project = Project.builder()
                .maxStorageSize(BigInteger.valueOf(2_000_000L))
                .build();
        BigInteger newStorageSize = BigInteger.valueOf(2_001_000L);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.checkProjectStorageSizeExceeded(newStorageSize, project));
    }

    @Test
    void testCheckUserInProject_UserEqualsTeamMember_Positive() {
        long userId = 1L;
        TeamMember teamMember = TeamMember.builder()
                .userId(1L)
                .build();
        Project project = new Project();
        Assertions.assertDoesNotThrow(() -> resourceValidator.checkUserInProject(userId, teamMember, project));
    }

    @Test
    void testCheckUserInProject_RoleEqualsManager_Positive() {
        long userId = 1L;
        TeamMember teamMember = TeamMember.builder()
                .userId(2L)
                .roles(new ArrayList<>(List.of(TeamRole.MANAGER)))
                .build();
        Project project = new Project();
        Assertions.assertDoesNotThrow(() -> resourceValidator.checkUserInProject(userId, teamMember, project));
    }

    @Test
    void testCheckUserInProject_UserEqualsOwnerProject_Positive() {
        long userId = 1L;
        TeamMember teamMember = TeamMember.builder()
                .userId(2L)
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();
        Project project = Project.builder()
                .ownerId(1L)
                .build();
        Assertions.assertDoesNotThrow(() -> resourceValidator.checkUserInProject(userId, teamMember, project));
    }

    @Test
    void testCheckUserInProject_UserEqualsTeamMember_Negative() {
        long userId = 1L;
        TeamMember teamMember = TeamMember.builder()
                .userId(2L)
                .roles(new ArrayList<>(List.of(TeamRole.DEVELOPER)))
                .build();
        Project project = Project.builder()
                .ownerId(2L)
                .build();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> resourceValidator.checkUserInProject(userId, teamMember, project));
    }
}