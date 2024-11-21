package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.StageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/stage")
@RequiredArgsConstructor
public class StageController {
    private final StageService stageService;

    @PostMapping
    public StageDto create(@RequestBody StageDto stage){
        validate(stage);
        return stageService.create(stage);
    }

    @GetMapping("/{role}")
    public List<StageDto> getByRole(@RequestBody @Valid StageDto stage,
                                    @PathVariable TeamRole role) {
        return stageService.getByRole(stage, role);
    }

    @GetMapping("/{status}")
    public List<StageDto> getByStatus(@RequestBody @Valid StageDto stage,
                                      @PathVariable TaskStatus status) {
        return stageService.getByStatus(stage, status);
    }

    @DeleteMapping("/delete-with-tasks")
    public StageDto deleteCascade(@RequestBody @Valid StageDto stage){
        return stageService.deleteCascade(stage);
    }

    @PostMapping("/postpone/{nextStageId}")
    public StageDto postponeTasks(@RequestBody @Valid StageDto stage,
                                  @PathVariable Long nextStageId){
        return stageService.postponeTasks(stage, nextStageId);
    }

    @PostMapping("/update")
    public StageDto update(@RequestBody @Valid StageDto stage){
        return stageService.update(stage);
    }

    @GetMapping("/projectId/stages")
    public List<StageDto> getAllStages(@PathVariable Long projectId){
        return stageService.getAllStages(projectId);
    }

    @GetMapping("/{stageId}")
    public StageDto getById(@PathVariable Long stageId){
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

