package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.service.StageService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    public StageDto create(StageDto stage){
        validate(stage);
        return stageService.create(stage);
    }

    public StageDto getByRole(StageDto stage, @NonNull TeamRole role) {
        return stageService.getByRole(stage, role);
    }

    public StageDto getByStatus(StageDto stage, TaskStatus status) {
        return stageService.getByStatus(stage, status);
    }

    public StageDto deleteCascade(StageDto stage){
        return stageService.deleteCascade(stage);
    }

    public StageDto postponeLinked(StageDto stage, Long anotherStageId){
        return stageService.postponeLinked(stage, anotherStageId);
    }

    public StageDto update(StageDto stage){
        return stageService.update(stage);
    }

    public List<StageDto> getAllStages(StageDto stage){
        return stageService.getAllStages(stage);
    }

    public StageDto getById(Long stageId){
        return stageService.getById(stageId);
    }

    private void validate(StageDto stage) {
        if (Strings.isEmpty(stage.getStageName())) {
            log.error("Пустое название");
            throw new DataValidationException("Название не может быть пустым");
        }

        List<StageRolesDto> roles = stage.getStageRolesDto();
        if (roles.isEmpty()) {
            log.error("Пустой список ролей");
            throw new DataValidationException("Список ролей пуст");
        }
        for (StageRolesDto role : roles) {
            if (role.getTeamRole() == null) {
                log.error("Отсутствует роль");
                throw new DataValidationException("Отсутствует роль");
            } else if (role.getCount() < 1) {
                log.error("Количество исполнителей роли меньше 1");
                throw new DataValidationException("Количество исполнителей роли не может быть менее 1");
            }
        }
    }
}

