package faang.school.projectservice.dto.filter;

import faang.school.projectservice.dto.internship.InternshipStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternshipFilterDto {

    private InternshipStatusDto statusPattern;
}
