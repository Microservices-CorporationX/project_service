package faang.school.projectservice.validator.resource;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Executable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorForResourceServiceTest {
    private long userId = 1L;
    private Resource resource = new Resource();
    private TeamMember teamMember = new TeamMember();

    @BeforeEach
    void setUp(){
        resource.setCreatedBy(teamMember);
    }
    @Test
    void testCheckStorageForEnoughMemory_ThrowsException_WhenCurrentStorageExceedsMax() {

        BigInteger currentStorageSize = BigInteger.valueOf(200);
        BigInteger maxStorageSize = BigInteger.valueOf(100);

        assertThrows(IllegalArgumentException.class,
                () -> ValidatorForResourceService.checkStorageForEnoughMemory(currentStorageSize, maxStorageSize));
    }

    @Test
    void testCheckStorageForEnoughMemory_DoesNotThrow_WhenStorageIsWithinLimits() {

        BigInteger currentStorageSize = BigInteger.valueOf(50);
        BigInteger maxStorageSize = BigInteger.valueOf(100);

        assertDoesNotThrow(() -> ValidatorForResourceService
                .checkStorageForEnoughMemory(currentStorageSize, maxStorageSize));
    }

    @Test
    void testCheckAccessForRemovalNotOwnerAndNotManager() {
        teamMember.setUserId(3L);
        List<TeamRole> teamRoles = List.of(TeamRole.INTERN);

        assertThrows(IllegalArgumentException.class,
                () -> ValidatorForResourceService.checkAccessForRemoval(userId, resource, teamRoles));
    }

    @Test
    void testCheckAccessForRemovalIsOwner() {
        teamMember.setUserId(1L);
        List<TeamRole> teamRoles = List.of(TeamRole.INTERN);

        assertDoesNotThrow(() -> ValidatorForResourceService.checkAccessForRemoval(userId, resource, teamRoles));
    }

    @Test
    void testCheckAccessForRemovalIsManager() {
        teamMember.setUserId(3L);
        List<TeamRole> teamRoles = List.of(TeamRole.MANAGER);

        assertDoesNotThrow(() -> ValidatorForResourceService.checkAccessForRemoval(userId, resource, teamRoles));
    }
}