package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.dto.FilterDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VacancyFilterDto implements FilterDto {
    Long id;
    String namePattern;
}
