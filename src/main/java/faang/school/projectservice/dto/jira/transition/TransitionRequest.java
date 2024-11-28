package faang.school.projectservice.dto.jira.transition;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TransitionRequest(
        @JsonProperty("transition")
        TransitionNested transition
) {
    public record TransitionNested(
            @JsonProperty("id")
            String id
    ) {}
}
