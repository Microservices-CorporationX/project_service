package faang.school.projectservice.dto.client.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class InternshipFilterDto {

    private InternshipStatus internshipStatus;
    private
}
