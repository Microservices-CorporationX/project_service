package faang.school.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CandidateAddDto {
    private List<Long> candidatesIds;
    private Long projectId;
    private Long vacancyId;
}
