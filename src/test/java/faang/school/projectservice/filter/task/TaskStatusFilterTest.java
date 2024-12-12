package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TaskStatusFilterTest {

    private TaskStatusFilter taskStatusFilter;
    private Task firstTask;
    private Task secondTask;
    private Task thirdTask;
    private List<Task> tasks;

    @BeforeEach
    public void setUp() {
        taskStatusFilter = new TaskStatusFilter();

        firstTask = Task.builder()
                .status(TaskStatus.IN_PROGRESS)
                .name("first task")
                .build();
        secondTask = Task.builder()
                .status(TaskStatus.DONE)
                .name("second task")
                .build();
        thirdTask = Task.builder()
                .status(TaskStatus.IN_PROGRESS)
                .name("third task")
                .build();
        tasks = List.of(firstTask, secondTask, thirdTask);
    }

    @Test
    public void testIsApplicable() {
        // arrange
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .status(TaskStatus.IN_PROGRESS)
                .build();
        boolean expected = true;

        // act
        boolean actual = taskStatusFilter.isApplicable(filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testIsNotApplicable() {
        // arrange
        TaskFilterDto filterDto = TaskFilterDto.builder().build();
        boolean expected = false;

        // act
        boolean actual = taskStatusFilter.isApplicable(filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApply() {
        // arrange
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .status(TaskStatus.IN_PROGRESS)
                .build();

        List<Task> expected = List.of(firstTask, thirdTask);

        // act
        List<Task> actual = taskStatusFilter.apply(tasks, filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApplyNoMatches() {
        // arrange
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .status(TaskStatus.CANCELLED)
                .build();

        List<Task> expected = new ArrayList<>();

        // act
        List<Task> actual = taskStatusFilter.apply(tasks, filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApplyEmptyTasks() {
        // arrange
        List<Task> tasks = new ArrayList<>();
        TaskFilterDto filterDto = TaskFilterDto.builder()
                .status(TaskStatus.IN_PROGRESS)
                .build();

        List<Task> expected = new ArrayList<>();

        // act
        List<Task> actual = taskStatusFilter.apply(tasks, filterDto);

        // assert
        assertEquals(expected, actual);
    }
}
