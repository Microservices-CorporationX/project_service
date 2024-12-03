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
class TaskPerformerUserIdFilterTest {
    @InjectMocks
    private TaskPerformerUserIdFilter filter = new TaskPerformerUserIdFilter();

    @Test
    void isApplicableShouldReturnTrueWhenPerformerUserIdIsNotNull() {
        TaskFiltersDto filters = new TaskFiltersDto();
        filters.setPerformerUserId(1L);

        assertEquals(true, filter.isApplicable(filters));
    }

    @Test
    void applyShouldFilterTasksByPerformerUserId() {
        Task firstTask = Task.builder().name("Task 1").performerUserId(1L).build();
        Task secondTask = Task.builder().name("Task 2").performerUserId(2L).build();
        Task thirdTask = Task.builder().name("Task 3").performerUserId(1L).build();

        TaskFiltersDto filters = new TaskFiltersDto();
        filters.setPerformerUserId(1L);

        List<Task> filteredTasks = filter.apply(Stream.of(firstTask, secondTask, thirdTask), filters).toList();

        assertEquals(2, filteredTasks.size());
        assertEquals(1L, filteredTasks.get(0).getPerformerUserId());
        assertEquals(1L, filteredTasks.get(1).getPerformerUserId());
    }
}