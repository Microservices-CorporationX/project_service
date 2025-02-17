package ru.corporationx.projectservice.validator.internship;

import ru.corporationx.projectservice.model.entity.Internship;
import ru.corporationx.projectservice.model.entity.InternshipStatus;
import ru.corporationx.projectservice.model.entity.TaskStatus;
import ru.corporationx.projectservice.model.entity.TeamMember;
import ru.corporationx.projectservice.model.entity.TeamRole;
import ru.corporationx.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskStatusValidator {

    private final TeamMemberRepository teamMemberRepository;

    public Internship checkingInternsTaskStatus(Internship internship) {
        if (InternshipStatus.COMPLETED.equals(internship.getStatus())) {
            return changeInternsRoleIfInternshipCompleted(internship);
        } else {
            return changeInternsRoleIfInternshipInProgress(internship);
        }
    }

    private Internship changeInternsRoleIfInternshipCompleted(Internship internship) {
        List<TeamMember> interns = internship.getInterns();

        List<Long> idsToDelete = interns.stream()
                .filter(this::hasUncompletedTasks)
                .map(TeamMember::getId)
                .toList();

        interns.removeIf(member -> idsToDelete.contains(member.getId()));
        idsToDelete.forEach(teamMemberRepository::deleteById);

        internship.setInterns(interns);
        return internship;
    }


    private Internship changeInternsRoleIfInternshipInProgress(Internship internship) {
        List<TeamMember> interns = internship.getInterns();

        List<Long> idsToDelete = interns.stream()
                .filter(this::compareCountOfTotalAndCompletedTasks)
                .map(TeamMember::getId)
                .toList();

        interns.removeIf(member -> idsToDelete.contains(member.getId()));
        idsToDelete.forEach(teamMemberRepository::deleteById);

        internship.setInterns(interns);
        return internship;
    }

    private boolean hasUncompletedTasks(TeamMember member) {
        boolean hasUncompletedTasks = member.getStages().stream()
                .flatMap(stage -> stage.getTasks().stream())
                .anyMatch(task -> !task.getStatus().equals(TaskStatus.DONE));

        if (hasUncompletedTasks) {
            return true;
        }

        member.getRoles().remove(TeamRole.INTERN);
        return false;
    }

    private boolean compareCountOfTotalAndCompletedTasks(TeamMember member) {
        long totalTasks = member.getStages().stream()
                .mapToLong(stage -> stage.getTasks().size())
                .sum();

        long doneTasks = member.getStages().stream()
                .flatMap(stage -> stage.getTasks().stream())
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();

        boolean hasTasks = totalTasks > 0;
        boolean allTasksDone = doneTasks == totalTasks;

        if (hasTasks && allTasksDone) {
            member.getRoles().remove(TeamRole.INTERN);
            return false;
        }

        return hasTasks && !allTasksDone;
    }
}
