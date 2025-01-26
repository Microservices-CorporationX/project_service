package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentCreateDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentReadDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.filter.moment.DateFilter;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.moment.MomentValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {
    private final static long momentId = 4L;
    private final static long userId = 7L;
    private final static long teamMemberId = 12L;
    private final static long projectId = 102L;
    private final static String projectName = "projectName";
    private final static long newUserId = 8L;
    private final static long newTeamMemberId = 13L;
    private final static long newProjectId = 103L;

    @Spy
    private MomentMapperImpl momentMapper;

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MomentValidator momentValidator;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private DateFilter dateFilter;

    private MomentService momentService;

    private List<MomentFilter> filters;

    @BeforeEach
    void setUp() {
        filters = List.of(dateFilter);
        momentService = new MomentService(momentMapper,
                momentRepository,
                projectService,
                projectRepository,
                momentValidator,
                teamMemberRepository,
                filters);
    }

    @Test
    void testCreate() {
        List<Long> projectIds = List.of(projectId);
        Project project = Project.builder().id(projectId).build();
        List<Project> projects = List.of(project);
        MomentCreateDto dto = MomentCreateDto.builder()
                .projectIds(projectIds)
                .name(projectName)
                .date(LocalDateTime.now())
                .build();
        Moment entity = momentMapper.toEntity(dto);
        entity.setProjects(projects);

        when(projectRepository.findAllById(any())).thenReturn(projects);
        when(momentRepository.save(entity)).thenReturn(entity);

        MomentReadDto result = momentService.create(dto);

        assertEquals(dto.getProjectIds(), result.getProjectIds());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDate(), result.getDate());
        verify(momentValidator).validateThatProjectsAreActive(dto.getProjectIds());
        verify(projectRepository).findAllById(dto.getProjectIds());
        verify(momentRepository).save(entity);
    }

    @Test
    void testSuccessUpdate_ifIdsFromDtoIsNotUnique() {
        Project project = Project.builder().id(projectId).build();
        TeamMember newTeamMember = TeamMember.builder().id(newTeamMemberId).userId(newUserId).build();
        TeamMember teamMember = TeamMember.builder().id(teamMemberId).userId(userId).build();
        List<TeamMember> teamMembers = Arrays.asList(teamMember, newTeamMember);
        List<Long> existingTeamMemberIds = Stream.of(teamMember).map(TeamMember::getId).toList();
        Team team = Team.builder()
                .teamMembers(teamMembers)
                .project(project)
                .build();
        teamMember.setTeam(team);
        newTeamMember.setTeam(team);
        Project newProject = Project.builder()
                .id(newProjectId)
                .teams(List.of(team))
                .build();
        List<Project> existingProjects = List.of(project);
        List<Project> newProjects = List.of(newProject);
        List<Project> expectedProjects = List.of(project, newProject);
        List<Long> expectedProjectIds = List.of(project, newProject).stream().map(Project::getId).toList();
        List<Long> expectedTeamMemberIds = List.of(teamMemberId, newTeamMemberId);

        MomentUpdateDto dto = MomentUpdateDto.builder()
                .momentId(momentId)
                .userIds(List.of(userId, newUserId))
                .projectIds(List.of(projectId, newProjectId))
                .build();

        Moment existingMoment = new Moment();
        existingMoment.setId(momentId);
        existingMoment.setTeamMemberIds(new ArrayList<>(existingTeamMemberIds));
        existingMoment.setProjects(new ArrayList<>(existingProjects));

        Moment expectedMoment = new Moment();
        expectedMoment.setId(momentId);
        expectedMoment.setTeamMemberIds(expectedTeamMemberIds);
        expectedMoment.setProjects(expectedProjects);
        existingMoment.setDate(LocalDateTime.now());

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(existingMoment));
        when(teamMemberRepository.findByUserId(userId)).thenReturn(List.of(teamMember));
        when(teamMemberRepository.findByUserId(newUserId)).thenReturn(List.of(newTeamMember));
        when(projectRepository.findAllById(any())).thenReturn(new ArrayList<>(newProjects));
        when(momentRepository.save(any())).thenReturn(expectedMoment);

        MomentReadDto result = momentService.update(dto);

        assertEquals(expectedProjectIds, result.getProjectIds());
        assertEquals(expectedTeamMemberIds, result.getTeamMemberIds());
        assertEquals(momentId, result.getId());
        verify(momentValidator).validateMomentUpdateDto(dto);
        verify(momentRepository).findById(momentId);
        verify(teamMemberRepository).findByUserId(userId);
        verify(momentRepository).save(existingMoment);
    }

    @Test
    void testSuccessUpdate_ifIdsFromDtoIsUnique() {
        Project project = Project.builder().id(projectId).build();
        TeamMember teamMember = TeamMember.builder().id(teamMemberId).userId(userId).build();
        List<TeamMember> teamMembers = Arrays.asList(teamMember);
        List<Long> existingTeamMemberIds = Stream.of(teamMember).map(TeamMember::getId).toList();
        Team team = Team.builder()
                .teamMembers(teamMembers)
                .project(project)
                .build();
        teamMember.setTeam(team);
        project.setTeams(List.of(team));
        List<Project> existingProjects = List.of(project);
        List<Project> expectedProjects = List.of(project);
        List<Long> expectedProjectIds = List.of(project).stream().map(Project::getId).toList();
        List<Long> expectedTeamMemberIds = List.of(teamMemberId);

        MomentUpdateDto dto = MomentUpdateDto.builder()
                .momentId(momentId)
                .userIds(List.of(userId))
                .projectIds(List.of(projectId))
                .build();

        Moment existingMoment = new Moment();
        existingMoment.setId(momentId);
        existingMoment.setTeamMemberIds(new ArrayList<>(existingTeamMemberIds));
        existingMoment.setProjects(new ArrayList<>(existingProjects));

        Moment expectedMoment = new Moment();
        expectedMoment.setId(momentId);
        expectedMoment.setTeamMemberIds(expectedTeamMemberIds);
        expectedMoment.setProjects(expectedProjects);
        existingMoment.setDate(LocalDateTime.now());

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(existingMoment));
        when(teamMemberRepository.findByUserId(userId)).thenReturn(List.of());
        when(momentRepository.save(any())).thenReturn(expectedMoment);
        when(projectRepository.findAllById(any())).thenReturn(new ArrayList<>());

        MomentReadDto result = momentService.update(dto);

        assertEquals(expectedProjectIds, result.getProjectIds());
        assertEquals(expectedTeamMemberIds, result.getTeamMemberIds());
        assertEquals(momentId, result.getId());
        verify(momentValidator).validateMomentUpdateDto(dto);
        verify(momentRepository).findById(momentId);
        verify(teamMemberRepository).findByUserId(userId);
        verify(momentRepository).save(existingMoment);
    }

    @Test
    void testSuccessUpdate_ifProjectIdsFromDtoIsEmpty() {
        Project project = Project.builder().id(projectId).build();
        TeamMember teamMember = TeamMember.builder().id(teamMemberId).userId(userId).build();
        List<TeamMember> teamMembers = Arrays.asList(teamMember);
        List<Long> existingTeamMemberIds = Stream.of(teamMember).map(TeamMember::getId).toList();
        Team team = Team.builder()
                .teamMembers(teamMembers)
                .project(project)
                .build();
        teamMember.setTeam(team);
        project.setTeams(List.of(team));
        List<Project> existingProjects = List.of(project);

        MomentUpdateDto dto = MomentUpdateDto.builder()
                .momentId(momentId)
                .projectIds(List.of())
                .userIds(List.of(userId))
                .build();

        Moment existingMoment = new Moment();
        existingMoment.setId(momentId);
        existingMoment.setTeamMemberIds(new ArrayList<>(existingTeamMemberIds));
        existingMoment.setProjects(new ArrayList<>(existingProjects));

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(existingMoment));
        when(teamMemberRepository.findByUserId(userId)).thenReturn(List.of());
        when(momentRepository.save(any())).thenReturn(existingMoment);

        momentService.update(dto);

        verify(momentValidator).validateMomentUpdateDto(dto);
        verify(momentRepository).findById(momentId);
        verify(teamMemberRepository).findByUserId(userId);
        verify(projectRepository, times(0)).findAllById(any());
        verify(momentRepository).save(existingMoment);
    }

    @Test
    void testSuccessUpdate_ifUsersIdsFromDtoIsEmpty() {
        Project project = Project.builder().id(projectId).build();
        TeamMember teamMember = TeamMember.builder().id(teamMemberId).userId(userId).build();
        List<TeamMember> teamMembers = Arrays.asList(teamMember);
        List<Long> existingTeamMemberIds = Stream.of(teamMember).map(TeamMember::getId).toList();
        Team team = Team.builder()
                .teamMembers(teamMembers)
                .project(project)
                .build();
        teamMember.setTeam(team);
        Project newProject = Project.builder()
                .id(newProjectId)
                .teams(List.of(team))
                .build();
        List<Project> existingProjects = List.of(project);
        List<Project> newProjects = List.of(newProject);

        MomentUpdateDto dto = MomentUpdateDto.builder()
                .momentId(momentId)
                .userIds(List.of())
                .projectIds(List.of(newProjectId))
                .build();

        Moment existingMoment = new Moment();
        existingMoment.setId(momentId);
        existingMoment.setTeamMemberIds(new ArrayList<>(existingTeamMemberIds));
        existingMoment.setProjects(new ArrayList<>(existingProjects));

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(existingMoment));
        when(projectRepository.findAllById(any())).thenReturn(new ArrayList<>(newProjects));
        when(momentRepository.save(any())).thenReturn(existingMoment);

        momentService.update(dto);

        verify(momentValidator).validateMomentUpdateDto(dto);
        verify(momentRepository).findById(momentId);
        verify(projectRepository).findAllById(any());
        verify(teamMemberRepository, times(0)).findByUserId(anyLong());
        verify(momentRepository).save(existingMoment);
    }

    @Test
    void testSuccessGetFilteredMoments() {
        MomentFilterDto filterDto = new MomentFilterDto();
        Moment moment = Moment.builder().projects(new ArrayList<>()).build();
        List<Moment> allMoments = List.of(moment);
        when(momentRepository.findAll()).thenReturn(allMoments);

        when(dateFilter.isApplicable(any())).thenReturn(true);
        when(dateFilter.apply(any(), any())).thenReturn(Stream.of(moment));

        List<MomentReadDto> results = momentService.getFilteredMoments(filterDto);

        assertEquals(allMoments.size(), results.size());
        verify(momentRepository).findAll();
        verify(dateFilter).isApplicable(any());
        verify(dateFilter).apply(any(), any());
    }

    @Test
    void testGetFilteredMomentsIfAllFiltersAreNotApplicable() {
        MomentFilterDto filterDto = new MomentFilterDto();
        Moment moment = Moment.builder().projects(new ArrayList<>()).build();
        List<Moment> allMoments = List.of(moment);
        when(momentRepository.findAll()).thenReturn(allMoments);

        when(dateFilter.isApplicable(any())).thenReturn(false);

        List<MomentReadDto> results = momentService.getFilteredMoments(filterDto);

        assertTrue(results.isEmpty());
        verify(momentRepository).findAll();
        verify(dateFilter).isApplicable(any());
        verify(dateFilter, times(0)).apply(any(), any());
    }

    @Test
    void testGetMoments() {
        Moment moment = Moment.builder().projects(new ArrayList<>()).build();
        List<Moment> allMoments = List.of(moment);
        when(momentRepository.findAll()).thenReturn(allMoments);

        List<MomentReadDto> results = momentService.getMoments();

        assertEquals(allMoments.size(), results.size());
        verify(momentRepository).findAll();
    }

    @Test
    void testSuccessGetMoment() {
        Moment expectedMoment = Moment.builder()
                .id(momentId).projects(new ArrayList<>()).build();
        when(momentRepository.findById(momentId))
                .thenReturn(Optional.of(expectedMoment));

        MomentReadDto result = momentService.getMoment(momentId);

        assertNotNull(result);
        verify(momentRepository).findById(momentId);
    }

    @Test
    void testSuccessGetMomentById() {
        Moment expectedMoment = Moment.builder().id(momentId).build();
        when(momentRepository.findById(momentId))
                .thenReturn(Optional.of(expectedMoment));

        Moment result = momentService.getMomentById(momentId);

        assertNotNull(result);
        verify(momentRepository).findById(momentId);
    }

    @Test
    void testGetMomentByIdThrowEntityNotFoundException() {
        when(momentRepository.findById(momentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> momentService.getMomentById(momentId));
    }

    @Test
    void testSuccessGetAllMoments() {
        Moment expectedMoment = Moment.builder().id(momentId).build();
        List<Moment> expectedMoments = List.of(expectedMoment);
        when(momentRepository.findAll()).thenReturn(expectedMoments);

        List<Moment> result = momentService.getAllMoments();

        assertEquals(expectedMoments, result);
    }
}