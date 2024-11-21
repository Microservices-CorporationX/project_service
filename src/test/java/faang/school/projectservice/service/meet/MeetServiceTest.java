package faang.school.projectservice.service.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetRequestDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.filter.meet.MeetFilter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.meet.MeetValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MeetServiceTest {
    static final Long CREATOR_ID = 1L;
    static final Long PROJECT_ID = 1L;
    static final Long MEET_ID = 1L;
    static final Long NONEXISTENT_MEET_ID = 999L;
    static final String MEET_TITLE = "Team Meeting";
    static final String MEET_DESCRIPTION = "Discuss project updates";

    @Mock
    private MeetRepository meetRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MeetMapper meetMapper;
    @Mock
    private MeetValidator meetValidator;
    @Mock
    private MeetFilter meetFilter;

    @InjectMocks
    private MeetService meetService;

    private List<MeetFilter> meetFilters;
    private MeetRequestDto meetRequestDto;
    private Meet meet;
    private Project project;
    private MeetResponseDto meetResponseDto;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        initializeTestDateTime();
        initializeMeetFilters();
        initializeMeetService();
        initializeTestEntities();
    }

    @Test
    @DisplayName("Should successfully create meet")
    void shouldSuccessfullyCreateMeet() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(true);
        when(meetMapper.toEntity(any(MeetRequestDto.class))).thenReturn(meet);
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);
        when(meetMapper.toDto(any(Meet.class))).thenReturn(meetResponseDto);
        MeetResponseDto result = meetService.createMeet(CREATOR_ID, meetRequestDto);
        verify(meetRepository).save(meet);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should throw exception when project doesn't exist")
    void shouldThrowExceptionForNonexistentProject() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(false);
        assertThrows(ProjectNotFoundException.class,
                () -> meetService.createMeet(CREATOR_ID, meetRequestDto));
        verify(projectRepository, never()).getProjectById(any());
        verify(meetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully update meet")
    void shouldSuccessfullyUpdateMeet() {
        when(meetRepository.findById(any())).thenReturn(Optional.of(meet));
        when(meetRepository.save(any(Meet.class))).thenReturn(meet);
        when(meetMapper.toDto(any(Meet.class))).thenReturn(meetResponseDto);
        MeetResponseDto result = meetService.updateMeet(CREATOR_ID, meetRequestDto);
        verify(meetValidator).validateMeetToUpdate(meet, CREATOR_ID);
        verify(meetRepository).save(meet);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should update meet status to CANCELLED")
    void shouldUpdateMeetStatusToCancelled() {
        MeetRequestDto cancelRequest = createCancelRequest();
        Meet existingMeet = createExistingMeet();
        setupMeetCancellation(existingMeet);
        MeetResponseDto result = meetService.updateMeet(CREATOR_ID, cancelRequest);
        verify(meetValidator).validateMeetToUpdate(existingMeet, CREATOR_ID);
        verify(meetRepository).save(argThat(savedMeet ->
                savedMeet.getStatus() == MeetStatus.CANCELLED &&
                        savedMeet.getUpdatedAt() != null
        ));
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(MeetStatus.CANCELLED);

    }

    @Test
    @DisplayName("Should throw exception when meet not found")
    void shouldThrowExceptionWhenMeetNotFound() {
        when(meetRepository.findById(NONEXISTENT_MEET_ID))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> meetService.findById(NONEXISTENT_MEET_ID));
        verify(meetRepository).findById(NONEXISTENT_MEET_ID);
        verify(meetMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should return filtered meets by project ID")
    void findAllByProjectIdFilter_shouldReturnFilteredMeets() {
        var filter = MeetFilterDto.builder().build();
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(meetFilter.isApplicable(filter)).thenReturn(true);
        when(meetFilter.apply(any(), any())).thenAnswer(invocation -> Stream.of(meet));
        var result = meetService.findAllByProjectIdFilter(PROJECT_ID, filter);
        verify(meetMapper).toDto(meet);
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should successfully find meet by ID")
    void shouldSuccessfullyFindMeetById() {
        when(meetRepository.findById(MEET_ID)).thenReturn(Optional.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetResponseDto);
        MeetResponseDto result = meetService.findById(MEET_ID);
        verify(meetRepository).findById(MEET_ID);
        verify(meetMapper).toDto(meet);
        assertThat(result)
                .isNotNull()
                .isEqualTo(meetResponseDto);
    }

    @Test
    @DisplayName("Should find meets with date filter")
    void shouldFindMeetsWithDateFilter() {
        MeetFilterDto filterDto = MeetFilterDto.builder()
                .createdAt(testDateTime)
                .build();
        setupFilterTest();
        List<MeetResponseDto> results = meetService.findByFilter(filterDto);
        assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("Should find meets with title filter")
    void shouldFindMeetsWithTitleFilter() {
        MeetFilterDto filterDto = MeetFilterDto.builder()
                .titlePattern("test")
                .build();
        setupFilterTest();
        List<MeetResponseDto> results = meetService.findByFilter(filterDto);
        assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("Should successfully delete meet when creator ID matches")
    void shouldSuccessfullyDeleteMeet() {
        when(meetRepository.findById(MEET_ID)).thenReturn(Optional.of(meet));
        meetService.deleteMeet(CREATOR_ID, MEET_ID);
        verify(meetValidator).validateMeetToDelete(meet, CREATOR_ID);
        verify(meetRepository).delete(meet);
    }

    @Test
    @DisplayName("Test stream reduction in findByFilter - combining filters")
    void findByFilter_streamReduction() {
        MeetFilterDto filterDto = MeetFilterDto.builder()
                .titlePattern("Team")
                .createdAt(LocalDateTime.now())
                .build();
        Meet meet1 = Meet.builder()
                .id(1L)
                .title("Team Meeting")
                .createdAt(LocalDateTime.now().plusHours(1))
                .build();
        Meet meet2 = Meet.builder()
                .id(2L)
                .title("Team Workshop")
                .createdAt(LocalDateTime.now().plusHours(2))
                .build();
        List<Meet> meets = Arrays.asList(meet1, meet2);
        MeetFilter firstFilter = mock(MeetFilter.class);
        when(firstFilter.isApplicable(filterDto)).thenReturn(true);
        when(firstFilter.apply(any(), eq(filterDto))).thenReturn(Stream.of(meet1, meet2));
        MeetFilter secondFilter = mock(MeetFilter.class);
        when(secondFilter.isApplicable(filterDto)).thenReturn(true);
        when(secondFilter.apply(any(), eq(filterDto))).thenReturn(Stream.of(meet2));
        meetService = new MeetService(meetRepository, projectRepository, meetMapper, meetValidator,
                Arrays.asList(firstFilter, secondFilter));
        when(meetRepository.findAll()).thenReturn(meets);
        when(meetMapper.toDto(any(Meet.class))).thenReturn(MeetResponseDto.builder().build());
        List<MeetResponseDto> results = meetService.findByFilter(filterDto);
        verify(firstFilter).isApplicable(filterDto);
        verify(secondFilter).isApplicable(filterDto);
        verify(firstFilter).apply(any(), eq(filterDto));
        verify(secondFilter).apply(any(), eq(filterDto));
        assertThat(results).hasSize(1);
    }

    private void initializeTestDateTime() {
        testDateTime = LocalDateTime.now();
    }

    private void initializeMeetFilters() {
        meetFilters = List.of(meetFilter);
    }

    private void initializeMeetService() {
        meetService = new MeetService(
                meetRepository,
                projectRepository,
                meetMapper,
                meetValidator,
                meetFilters
        );
    }

    private void initializeTestEntities() {
        meet = createTestMeet();
        meetRequestDto = createTestMeetRequestDto();
        project = createTestProject();
        meetResponseDto = createTestMeetResponseDto();
    }

    private Meet createTestMeet() {
        return Meet.builder()
                .id(MEET_ID)
                .title(MEET_TITLE)
                .description(MEET_DESCRIPTION)
                .status(MeetStatus.PENDING)
                .creatorId(CREATOR_ID)
                .createdAt(testDateTime)
                .build();
    }

    private MeetRequestDto createTestMeetRequestDto() {
        return MeetRequestDto.builder()
                .id(MEET_ID)
                .title(MEET_TITLE)
                .description(MEET_DESCRIPTION)
                .status(MeetStatus.PENDING)
                .projectId(PROJECT_ID)
                .build();
    }

    private Project createTestProject() {
        return Project.builder()
                .id(PROJECT_ID)
                .meets(List.of(meet))
                .build();
    }

    private MeetResponseDto createTestMeetResponseDto() {
        return MeetResponseDto.builder()
                .id(MEET_ID)
                .title(MEET_TITLE)
                .description(MEET_DESCRIPTION)
                .status(MeetStatus.PENDING)
                .build();
    }

    private MeetRequestDto createCancelRequest() {
        return MeetRequestDto.builder()
                .id(MEET_ID)
                .title(MEET_TITLE)
                .description(MEET_DESCRIPTION)
                .status(MeetStatus.CANCELLED)
                .projectId(PROJECT_ID)
                .build();
    }

    private Meet createExistingMeet() {
        return Meet.builder()
                .id(MEET_ID)
                .title(MEET_TITLE)
                .description(MEET_DESCRIPTION)
                .status(MeetStatus.PENDING)
                .creatorId(CREATOR_ID)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void setupMeetCancellation(Meet existingMeet) {
        when(meetRepository.findById(MEET_ID)).thenReturn(Optional.of(existingMeet));
        when(meetRepository.save(any(Meet.class))).thenReturn(existingMeet);
        when(meetMapper.toDto(any(Meet.class))).thenReturn(
                MeetResponseDto.builder()
                        .id(MEET_ID)
                        .status(MeetStatus.CANCELLED)
                        .build()
        );
    }

    private void setupFilterTest() {
        when(meetRepository.findAll()).thenReturn(List.of(meet));
        when(meetMapper.toDto(meet)).thenReturn(meetResponseDto);
    }
}