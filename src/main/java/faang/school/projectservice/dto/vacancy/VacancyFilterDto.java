package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.dto.filter.FilterDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacancyFilterDto implements FilterDto {

    private String namePattern;
}