package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validation.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private TeamMemberRepository memberRepository;
    @Mock
    private VacancyValidator validator;
    @InjectMocks
    private VacancyService service;

    @BeforeEach
    public void setUp() {
        VacancyMapper mapper = Mappers.getMapper(VacancyMapper.class);
        service = new VacancyService(vacancyRepository, memberRepository, validator, mapper);
    }

    @Test
    public void createVacancy_Success() {
        VacancyDto vacancy = new VacancyDto(1L, TeamRole.DESIGNER, 2, 13L);

        TeamMember creator = new TeamMember(
                13L,
                13L,
                "Bob",
                List.of(TeamRole.OWNER, TeamRole.DEVELOPER, TeamRole.ANALYST),
                new Team(),
                Collections.emptyList());

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(creator));

        service.createVacancy(vacancy);
        verify(vacancyRepository).save(any(Vacancy.class));
    }
}
