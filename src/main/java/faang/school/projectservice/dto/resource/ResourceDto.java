package faang.school.projectservice.dto.resource;

import java.math.BigInteger;

public record ResourceDto(
        Long id,
        String name,
        BigInteger size,
        Long createdBy,
        Long updatedBy,
        String createdAt,
        String updatedAt
) {
}
