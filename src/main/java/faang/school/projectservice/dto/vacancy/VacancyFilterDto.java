package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.dto.FilterDto;
import lombok.Data;

@Data
public class VacancyFilterDto implements FilterDto {
    Long id;
    String namePattern;
}
