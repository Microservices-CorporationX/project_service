package faang.school.projectservice.validator.resource;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigInteger;
import java.util.List;

public class ValidatorForResourceService {
    public static void checkStorageForEnoughMemory(@NotNull BigInteger currentStorageSize,@NotNull BigInteger maxStorageSize) {
        if (currentStorageSize.compareTo(maxStorageSize) > 0) {
            throw new IllegalArgumentException("Ошибка, недостаточно памяти!");
        }
    }

    public static void checkAccessForRemoval(@Min(0) long userId, @NotNull Resource resource, @NotNull List<TeamRole> teamRoles) {
        if (userId != resource.getCreatedBy().getUserId() && !teamRoles.contains(TeamRole.MANAGER)){
            throw new IllegalArgumentException("Ошибка доступа, только авто или менеджер могут удалить файл");
        }
    }
}
