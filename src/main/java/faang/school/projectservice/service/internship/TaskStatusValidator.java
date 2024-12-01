package faang.school.projectservice.service.internship;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class TaskStatusValidator {
    private final TeamMemberRepository teamMemberRepository;

    public Internship checkingInternsTaskStatus(Internship internship) {
        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            return changeInternsRoleIfInternshipCompleted(internship);
        } else {
            return changeInternsRoleIfInternshipInProgress(internship);
        }
    }

    private Internship changeInternsRoleIfInternshipCompleted(Internship internship) {
        List<TeamMember> interns = internship.getInterns();
        Iterator<TeamMember> iterator = interns.iterator();
        while (iterator.hasNext()) {
            TeamMember member = iterator.next();
            boolean hasUncompletedTask = member.getStages().stream()
                    .flatMap(stage -> stage.getTasks().stream())
                    .anyMatch(task -> !task.getStatus().equals(TaskStatus.DONE));
            if (hasUncompletedTask) {
                iterator.remove();
                teamMemberRepository.deleteById(member.getId());
            } else {
                member.getRoles().remove(TeamRole.INTERN);
            }
        }
        internship.setInterns(interns);
        return internship;
    }

    private Internship changeInternsRoleIfInternshipInProgress(Internship internship) {
        AtomicInteger countOfAllTasks = new AtomicInteger();
        AtomicInteger countOfDoneTasks = new AtomicInteger();
        List<TeamMember> interns = internship.getInterns();
        Iterator<TeamMember> iterator = interns.iterator();
        while (iterator.hasNext()) {
            TeamMember member = iterator.next();
            boolean hasUncompletedTask = member.getStages().stream()
                    .flatMap(stage -> stage.getTasks().stream())
                    .peek(task -> {
                        countOfAllTasks.getAndIncrement();
                        if (task.getStatus() == TaskStatus.DONE) {
                            countOfDoneTasks.getAndIncrement();
                        }
                    })
                    .anyMatch(task -> !task.getStatus().equals(TaskStatus.DONE));
            if (hasUncompletedTask) {
                iterator.remove();
                teamMemberRepository.deleteById(member.getId());
                countOfAllTasks.set(0);
                countOfDoneTasks.set(0);
            }
            if (!hasUncompletedTask && countOfAllTasks.get() == countOfDoneTasks.get() && countOfAllTasks.get() != 0) {
                member.getRoles().remove(TeamRole.INTERN);
                countOfAllTasks.set(0);
                countOfDoneTasks.set(0);
            }
        }
        internship.setInterns(interns);
        return internship;
    }
}
