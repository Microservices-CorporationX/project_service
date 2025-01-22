package faang.school.projectservice.dto.moment;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MomentUpdateDto {

    @NotNull
    @Positive
    private Long momentId;

    private List<Long> projectIds;

    private List<Long> userIds;
}
