package faang.school.projectservice.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserIdsDto {

    @NotEmpty(message = "UserIds list must not be empty.")
    private List<@NotNull(message = "User ID must not be null.") Long> userIds;
}