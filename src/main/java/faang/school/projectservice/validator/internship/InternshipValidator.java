package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internship.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InternshipValidator {
    private final InternshipService internshipService;
    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;

    public void validateInternshipCreation(InternshipDto internshipDto) {
        validateProjectExists(internshipDto);
        validateInterns(internshipDto);
        validateInternshipDuration(internshipDto);
        validateMentorBelongsProject(internshipDto);
    }

    public void validateInternshipUpdating(InternshipDto internshipDto) {
        validateAddingInterns(internshipDto);
    }

    public boolean validateInternshipCompleted(InternshipDto internshipDto) {
        List<Task> tasks = projectRepository.findById(internshipDto.getProjectId()).get().getTasks();

        if (internshipDto.getStatus().equals(InternshipStatus.COMPLETED)) {
            for (Task task : tasks) {
                if (!task.getStatus().equals(TaskStatus.DONE)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void validateProjectExists(InternshipDto internshipDto) {
        Long projectId = internshipDto.getProjectId();

        if (!projectRepository.existsById(projectId)) {
            String message = String.format("Проекта с ID %d не существует!", projectId);
            throw new EntityNotFoundException(message);
        }
    }

    private void validateInterns(InternshipDto internshipDto) {
        List<Long> interns = internshipDto.getInternsIds();
        boolean flag = true;

        for (Long id : interns) {
            if (id == null || id == 0) {
                flag = false;
                break;
            }
        }

        if (!flag) {
            String message = "Список стажирующихся не может быть пустым!";
            throw new EntityNotFoundException(message);
        }
    }

    private void validateInternshipDuration(InternshipDto internshipDto) {
        LocalDateTime startDate = internshipDto.getStartDate();
        LocalDateTime endDate = internshipDto.getEndDate();

        if (startDate.plusMonths(3).isBefore(endDate)) {
            String message = "Стажировка не может длиться дольше 3 месяцев!";
            throw new EntityNotFoundException(message);
        }
    }

    private void validateMentorBelongsProject(InternshipDto internshipDto) {
        Long mentorId = internshipDto.getMentorId();
        Project project = projectRepository.findById(internshipDto.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Такого проекта не существует!"));
        List<Team> team = project.getTeams();
        boolean flag = false;

        for (Team member : team) {
            if (member.getId().equals(mentorId)) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            String message = String.format("Ментор с ID %d не учавствует в проекте!", mentorId);
            throw new EntityNotFoundException(message);
        }
    }

    private void validateAddingInterns(InternshipDto internshipDto) {
        List<TeamMember> interns = internshipRepository.findById(internshipDto.getId()).get().getInterns();

        if (interns.size() < internshipDto.getInternsIds().size()) {
            String message = "Невозможно добавить новых пользователей, так как стажировка уже началась!";
            throw new EntityNotFoundException(message);
        }
    }
}
