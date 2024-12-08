package faang.school.projectservice.service.project.meet;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.meet.MeetDto;
import faang.school.projectservice.dto.project.meet.MeetFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.project.meet.MeetMapperImpl;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.project.meet.MeetValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetServiceTest {

    @InjectMocks
    private MeetService meetService;

    @Mock
    private MeetRepository meetRepository;

    @Mock
    private MeetValidator meetValidator;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserContext userContext;

    @Spy
    private MeetMapperImpl meetMapper;

    @Mock
    private Filter<MeetFilterDto, Stream<Meet>> filter;

    private List<Filter<MeetFilterDto, Stream<Meet>>> meetFilters;

    private MeetDto meetDto;

    private long userId;

    @BeforeEach
    public void setUp() {
        meetDto = new MeetDto();
        meetDto.setId(1L);
        meetDto.setProjectId(1L);
        userId = 1L;

        meetFilters = new ArrayList<>();
        meetFilters.add(filter);
        meetFilters.add(filter);

        meetService = new MeetService(
                meetRepository,
                meetValidator,
                projectService,
                userContext, meetMapper,
                meetFilters
        );
    }

    @Test
    public void testCreateMeet() {
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(meetValidator).validate(meetDto, userId, projectService);
        when(projectService.getProjectEntityById(meetDto.getProjectId())).thenReturn(new Project());

        meetService.createMeet(meetDto);

        verify(meetRepository).save(any());
    }

    @Test
    public void testUpdateMeetEntityNotFound() {
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(meetValidator).validate(meetDto, userId, projectService);
        when(meetRepository.findByIdAndCreatorId(meetDto.getId(), userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> meetService.updateMeet(meetDto));
    }

    @Test
    public void testUpdateMeet() {
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(meetValidator).validate(meetDto, userId, projectService);
        when(meetRepository.findByIdAndCreatorId(meetDto.getId(), userId)).thenReturn(Optional.of(new Meet()));

        meetService.updateMeet(meetDto);

        verify(meetRepository).save(any());
    }

    @Test
    public void testGetMeets() {
        long projectId = 1L;

        MeetFilterDto meetFilterDto = new MeetFilterDto();
        List<Meet> meets = List.of(new Meet(), new Meet());

        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(meetValidator).validate(projectId, userId, projectService);
        when(meetRepository.findAllByProjectId(projectId)).thenReturn(meets);
        when(filter.isApplicable(meetFilterDto)).thenReturn(true);
        when(filter.apply(any(), any())).thenReturn(Stream.of(new Meet()));

        List<MeetDto> result = meetService.getMeets(projectId, meetFilterDto);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetMeetByIdAndUserId() {
        long meetId = 1L;

        when(userContext.getUserId()).thenReturn(userId);
        when(meetRepository.findByIdAndCreatorId(meetId, userId)).thenReturn(Optional.of(new Meet()));

        meetService.getMeetByIdAndUserId(meetId);
    }

    @Test
    public void testGetMeetByIdAndUserIdEntityNotFound() {
        long meetId = 1L;

        when(userContext.getUserId()).thenReturn(userId);
        when(meetRepository.findByIdAndCreatorId(meetId, userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> meetService.getMeetByIdAndUserId(meetId));
    }
}
