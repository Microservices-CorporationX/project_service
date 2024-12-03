package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFiltersDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskStatusFilterTest {
    @InjectMocks
    private TaskStatusFilter filter = new TaskStatusFilter();

    @Test
    void isApplicableShouldReturnTrueWhenStatusIsNotNull() {
        TaskFiltersDto filters = new TaskFiltersDto();
        filters.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(true, filter.isApplicable(filters));
    }

    @Test
    void applyShouldFilterTasksCorrectly() {
        Task firstTask = Task.builder().name("Task 1").status(TaskStatus.DONE).build();
        Task secondTask = Task.builder().name("Task 2").status(TaskStatus.IN_PROGRESS).build();
        Task thirdTask = Task.builder().name("Task 3").status(TaskStatus.DONE).build();

        TaskFiltersDto filters = new TaskFiltersDto();
        filters.setStatus(TaskStatus.DONE);

        List<Task> filteredTasks = filter.apply(Stream.of(firstTask, secondTask, thirdTask), filters).toList();

        assertEquals(2, filteredTasks.size());
        assertEquals(TaskStatus.DONE, filteredTasks.get(0).getStatus());
        assertEquals(TaskStatus.DONE, filteredTasks.get(1).getStatus());
    }
}