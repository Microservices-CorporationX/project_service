package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TaskPerformerFilterTest {

    private TaskPerformerFilter taskPerformerFilter;
    private Task firstTask;
    private Task secondTask;
    private Task thirdTask;
    private List<Task> tasks;

    @BeforeEach
    public void setUp() {
        taskPerformerFilter = new TaskPerformerFilter();

        firstTask = Task.builder()
                .performerUserId(5L)
                .name("first task")
                .build();
        secondTask = Task.builder()
                .performerUserId(10L)
                .name("second task")
                .build();
        thirdTask = Task.builder()
                .performerUserId(5L)
                .name("third task")
                .build();
        tasks = List.of(firstTask, secondTask, thirdTask);
    }

    @Test
    public void testIsApplicable() {
        // arrange
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .performerUserId(5L)
                .build();
        boolean expected = true;

        // act
        boolean actual = taskPerformerFilter.isApplicable(filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testIsNotApplicable() {
        // arrange
        TaskFilterDto filterDto = TaskFilterDto.builder().build();
        boolean expected = false;

        // act
        boolean actual = taskPerformerFilter.isApplicable(filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApply() {
        // arrange
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .performerUserId(5L)
                .build();

        List<Task> expected = List.of(firstTask, thirdTask);

        // act
        List<Task> actual = taskPerformerFilter.apply(tasks, filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApplyNoMatches() {
        // arrange
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .performerUserId(1337L)
                .build();

        List<Task> expected = new ArrayList<>();

        // act
        List<Task> actual = taskPerformerFilter.apply(tasks, filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApplyEmptyTasks() {
        // arrange
        List<Task> tasks = new ArrayList<>();
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .performerUserId(5L)
                .build();

        List<Task> expected = new ArrayList<>();

        // act
        List<Task> actual = taskPerformerFilter.apply(tasks, filterDto);

        // assert
        assertEquals(expected, actual);
    }
}
