package faang.school.projectservice.dto.client;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VacancyFilterDto {
    private String name;
    private long count;
}
