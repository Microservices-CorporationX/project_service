package ru.corporationx.projectservice.validator.internship;

import ru.corporationx.projectservice.model.entity.Internship;
import ru.corporationx.projectservice.model.entity.InternshipStatus;
import ru.corporationx.projectservice.model.entity.Task;
import ru.corporationx.projectservice.model.entity.TaskStatus;
import ru.corporationx.projectservice.model.entity.TeamMember;
import ru.corporationx.projectservice.model.entity.TeamRole;
import ru.corporationx.projectservice.model.entity.stage.Stage;
import ru.corporationx.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.corporationx.projectservice.validator.internship.TaskStatusValidator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TaskStatusValidatorTest {
    @InjectMocks
    private TaskStatusValidator taskStatusValidator;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Test
    void testUpdatedInternshipIfInternshipStatusCompleted() {
        Internship expectedInternship = prepareData(InternshipStatus.COMPLETED, TaskStatus.DONE, TaskStatus.DONE, true);
        Internship internshipForChecking = prepareData(InternshipStatus.COMPLETED, TaskStatus.CANCELLED, TaskStatus.DONE, false);

        Internship actualInternship = taskStatusValidator.checkingInternsTaskStatus(internshipForChecking);

        assertEquals(expectedInternship, actualInternship);
    }

    @Test
    void testUpdatedInternshipIfInternshipStatusInProgress() {
        Internship expectedInternship = prepareData(InternshipStatus.IN_PROGRESS, TaskStatus.DONE, TaskStatus.DONE, true);
        Internship internshipForChecking = prepareData(InternshipStatus.IN_PROGRESS, TaskStatus.CANCELLED, TaskStatus.DONE, false);

        Internship actualInternship = taskStatusValidator.checkingInternsTaskStatus(internshipForChecking);

        assertEquals(expectedInternship, actualInternship);
    }


    private Internship prepareData(InternshipStatus status, TaskStatus firstStatus, TaskStatus secondStatus, boolean isExpected) {
        List<TeamMember> interns = new ArrayList<>();
        if (isExpected) {
            List<Task> tasksOfSecondStage = List.of(
                    Task.builder()
                            .status(firstStatus)
                            .build(),
                    Task.builder()
                            .status(secondStatus)
                            .build());
            List<Stage> secondStage = List.of(
                    Stage.builder()
                            .tasks(tasksOfSecondStage)
                            .build());
            List<TeamRole> roles = new ArrayList<>();
            roles.add(TeamRole.DEVELOPER);
            interns.add(TeamMember.builder()
                    .id(2L)
                    .stages(secondStage)
                    .roles(roles)
                    .build());
        } else {
            List<Task> tasksOfFirstStage = List.of(
                    Task.builder()
                            .status(firstStatus)
                            .build(),
                    Task.builder()
                            .status(secondStatus)
                            .build());
            List<Task> tasksOfSecondStage = List.of(
                    Task.builder()
                            .status(TaskStatus.DONE)
                            .build(),
                    Task.builder()
                            .status(TaskStatus.DONE)
                            .build());
            List<Stage> firstStage = List.of(
                    Stage.builder()
                            .tasks(tasksOfFirstStage)
                            .build());
            List<Stage> secondStage = List.of(
                    Stage.builder()
                            .tasks(tasksOfSecondStage)
                            .build());
            List<TeamRole> rolesOfFirstIntern = new ArrayList<>();
            rolesOfFirstIntern.add(TeamRole.TESTER);
            rolesOfFirstIntern.add(TeamRole.INTERN);
            TeamMember firstIntern = TeamMember.builder()
                    .id(1L)
                    .stages(firstStage)
                    .roles(rolesOfFirstIntern)
                    .build();
            List<TeamRole> rolesOfSecondIntern = new ArrayList<>();
            rolesOfSecondIntern.add(TeamRole.INTERN);
            rolesOfSecondIntern.add(TeamRole.DEVELOPER);
            TeamMember secondIntern = TeamMember.builder()
                    .id(2L)
                    .stages(secondStage)
                    .roles(rolesOfSecondIntern)
                    .build();
            interns.add(firstIntern);
            interns.add(secondIntern);
            return Internship.builder()
                    .status(status)
                    .interns(interns)
                    .build();
        }
        return Internship.builder()
                .status(status)
                .interns(interns)
                .build();
    }
}
