package faang.school.projectservice.controller;


import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.service.StageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects/stages")
public class StageController {

    private final StageMapper stageMapper;
    private final StageService stageService;


    @PostMapping("/stage")
    public ResponseEntity<StageDto> createStage( @RequestBody StageDto stageDto){
        return stageService.createStage(stageMapper.toStage(stageDto));
    };

    @GetMapping
    public ResponseEntity<List<StageDto>> getStages(@PathVariable Long projectId,
                                                    @RequestParam(required = false) Set<StageRoles> roles,
                                                    @RequestParam(required = false) TaskStatus taskStatus){
        return stageService.getStages(projectId);
    };

    @DeleteMapping("/{stageId}")
    public void deleteStage(@PathVariable Long stageId){
        stageService.deleteStage(stageId);
        log.info("Удален этап {}", stageId);
    };

    @PutMapping("/{stageId}")
    public ResponseEntity<StageUpdateDto> updateStage(@PathVariable Long stageId,
                                                      @RequestBody StageDto StageUpdateDto){
        return stageService.updateStage(stageId,StageUpdateDto);
    };

    @PostMapping("/{stageId}/invitations")
    public ResponseEntity<StageDto> sendInvitations(@PathVariable Long stageId,
                                                    @RequestBody StageDto stageDto){
        return stageService.sendInvitations(stageId,stageMapper.toStage(stageDto));
    };

    @GetMapping("/{stageId}")
    public ResponseEntity<StageDto> getStageDetails(@PathVariable Long stageId){
        return stageService.getStageDetails(stageId);
    };

    @GetMapping("{stageId}/tasks")
    public ResponseEntity<List<Task>> getStageTasks(@PathVariable Long stageId,
                                                    @RequestParam(required = false) TaskStatus status){
        return stageService.getStageTasks(stageId,status);
    };

    @PutMapping("{stageId}/participants")
    public ResponseEntity<StageUpdateDto> updateStageTeamMember(@PathVariable Long stageId,
                                                                  @RequestBody Set<StageUpdateDto> StageUpdateDto){
    return stageService.updateStageParticipants(StageUpdateDto);
    };
}
