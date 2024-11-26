package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.filter.moment.MomentFilterToFromDate;
import faang.school.projectservice.mapper.moment.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.moment.MomentServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {

    @Spy
    private MomentMapperImpl momentMapper;
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MomentServiceValidator momentServiceValidator;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private List<MomentFilter> momentFilters;
    @InjectMocks
    private MomentService momentService;

    @Test
    void testCreateMoment() {
        MomentDto momentDto = buildMomentDto();
        Moment moment = momentMapper.toEntity(momentDto);
        Project project = buildProject();
        Team team = buildTeam();
        team.setProject(project);
        TeamMember teamMember = buildTeamMember();
        teamMember.setTeam(team);
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(teamMemberRepository.findById(anyLong())).thenReturn(teamMember);

        MomentDto result = momentService.createMoment(momentDto);

        verify(teamMemberRepository, times(2)).findById(anyLong());
        verify(teamMemberRepository, times(2)).findById(anyLong());
        verify(momentRepository, times(1)).save(moment);
        assertEquals(momentDto, result);
    }

    @Test
    void testUpdateMoment() {
        MomentDto momentDto = buildMomentDto();
        Moment moment = momentMapper.toEntity(momentDto);
        Project project = buildProject();
        Team team = buildTeam();
        team.setProject(project);
        TeamMember teamMember = buildTeamMember();
        teamMember.setTeam(team);
        when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(teamMemberRepository.findById(anyLong())).thenReturn(teamMember);

        MomentDto result = momentService.updateMoment(1L, momentDto);

        assertEquals(momentDto, result);
        verify(momentRepository, times(1)).save(moment);
    }

    @Test
    void testDeleteMoment() {
        Moment moment = momentMapper.toEntity(buildMomentDto());
        when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));
        momentService.deleteMoment(1L);
        verify(momentRepository, times(1)).delete(moment);
    }

    @Test
    void testGetAllMoments() {
        MomentDto momentDto = buildMomentDto();
        Moment moment = momentMapper.toEntity(momentDto);
        List<Moment> moments = List.of(moment);
        when(momentRepository.findAll()).thenReturn(moments);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        List<MomentDto> result = momentService.getAllMoments();

        verify(momentRepository, times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(momentDto, result.get(0));
    }

    @Test
    void testGetMomentById() {
        MomentDto momentDto = buildMomentDto();
        Moment moment = momentMapper.toEntity(momentDto);
        when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto result = momentService.getMomentById(1L);

        verify(momentRepository, times(1)).findById(1L);
        assertEquals(momentDto, result);
    }

    @Test
    void testFilterMomentsByDate() {
        MomentDto momentDto = buildMomentDto();
        Moment moment = momentMapper.toEntity(momentDto);
        List<MomentFilter> filters = List.of(new MomentFilterToFromDate());
        when(momentRepository.findAll()).thenReturn(List.of(moment));
        when(momentFilters.stream()).thenReturn(filters.stream());

        List<MomentDto> result = momentService.filterMomentsByDate(buildFilterDto());

        verify(momentRepository, times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(momentDto, result.get(0));
    }

    @Test
    void updateMomentMomentNotFound() {
        MomentDto momentDto = buildMomentDto();
        when(momentRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> momentService.updateMoment(1L, momentDto));
        assertEquals("Moment doesn't exist by id: 1", exception.getMessage());
    }

    @Test
    void deleteMomentMomentNotFound() {
        when(momentRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> momentService.deleteMoment(1L));
        assertEquals("Moment doesn't exist by id: 1", exception.getMessage());
    }

    private MomentDto buildMomentDto() {
        return MomentDto.builder()
                .name("Test Moment")
                .description("Test Description")
                .date(LocalDateTime.now())
                .imageId("imageId")
                .projectIds(Arrays.asList(1L, 2L))
                .teamMemberIds(Arrays.asList(1L, 2L))
                .build();
    }

    private MomentFilterDto buildFilterDto() {
        return MomentFilterDto.builder()
                .fromDate(LocalDateTime.now().minusDays(1))
                .toDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    private Project buildProject() {
        return Project.builder()
                .id(1L)
                .name("Test Project")
                .teams(List.of(buildTeam()))
                .build();
    }

    private Team buildTeam() {
        return Team.builder()
                .id(1L)
                .teamMembers(List.of(buildTeamMember()))
                .build();
    }

    private TeamMember buildTeamMember() {
        return TeamMember.builder()
                .id(1L)
                .build();
    }
}