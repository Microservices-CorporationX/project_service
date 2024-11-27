package faang.school.projectservice.dto.resource;

import lombok.Builder;

import java.math.BigInteger;

@Builder
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
