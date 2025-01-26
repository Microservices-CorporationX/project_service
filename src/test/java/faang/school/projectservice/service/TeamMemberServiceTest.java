package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TeamMemberService teamMemberService;

    @Test
    public void shouldThrowNotFoundTeamMember() {
        Long id = 5L;

        Mockito.when(teamMemberRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> teamMemberService.findById(id),
                String.format("Этап c id: %d не найден", id));

        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(id);
    }

    @Test
    public void shouldFoundTeamMember() {
        Long id = 5L;

        TeamMember member = TeamMember.builder().id(id).build();

        Mockito.when(teamMemberRepository.findById(id)).thenReturn(Optional.of(member));

        Assertions.assertEquals(member, teamMemberService.findById(id));

        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(id);
    }
}