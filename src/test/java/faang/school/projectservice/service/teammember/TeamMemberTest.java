package faang.school.projectservice.service.teammember;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamMemberTest {

        @Mock
        private TeamMemberRepository teamMemberRepository;

        @InjectMocks
        private TeamMemberService teamMemberService;

        @Test
        public void findByIdTest() {
            Long id = 1L;
            TeamMember teamMember = new TeamMember();
            teamMember.setId(id);
            teamMember.setUserId(10L);
            when(teamMemberRepository.findById(id)).thenReturn(teamMember);

            TeamMember findMember = teamMemberService.findById(id);

            assertEquals(teamMember.getId(), findMember.getId());
            assertEquals(teamMember.getUserId(), findMember.getUserId());
        }
    }