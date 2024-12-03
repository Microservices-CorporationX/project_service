package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFiltersDto;
import faang.school.projectservice.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskTextFilterTest {
    @InjectMocks
    private TaskTextFilter filter;

    @Test
    void isApplicableShouldReturnTrueWhenTextIsNotNullOrBlank() {
        TaskFiltersDto filters = new TaskFiltersDto();
        filters.setText("test");

        assertEquals(true, filter.isApplicable(filters));
    }

    @Test
    void apply_shouldFilterTasksCorrectly() {
        Task firstTask = Task.builder().name("Task 1").description("Description 1").build();
        Task secondTask = Task.builder().name("Another Task").description("Some text").build();

        TaskFiltersDto filters = new TaskFiltersDto();
        filters.setText("task");

        List<Task> filteredTasks = filter.apply(Stream.of(firstTask, secondTask), filters).toList();

        assertEquals(2, filteredTasks.size());
        assertEquals("Task 1", filteredTasks.get(0).getName());
        assertEquals("Another Task", filteredTasks.get(1).getName());
    }
}