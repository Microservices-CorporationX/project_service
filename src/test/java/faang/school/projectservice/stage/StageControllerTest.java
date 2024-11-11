package faang.school.projectservice.stage;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.controller.StageController;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.exception.InvalidStageTransferException;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.DeletionType;
import faang.school.projectservice.service.stage.StageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StageController.class)
public class StageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StageService stageService;

    @MockBean
    private UserContext userContext;

    @Test
    void testCreateStageSuccessful() throws Exception {
        StageRolesDto stageRole = createStageRoleDto(null, TeamRole.TESTER, 1, null);
        StageDto stageDto = createStageDto(null, "Stage name", 1L, List.of(stageRole));

        when(stageService.createStage(stageDto)).thenReturn(stageDto);

        mockMvc.perform(post("/stages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageId").value(stageDto.getStageId()))
                .andExpect(jsonPath("$.stageName").value(stageDto.getStageName()))
                .andExpect(jsonPath("$.projectId").value(stageDto.getProjectId()))
                .andExpect(jsonPath("$.stageRoles").isNotEmpty());
    }

    @Test
    void testCreateStageBadRequest() throws Exception {
        StageDto stageDto = createStageDto(null, "Stage name", 1L, null);

        mockMvc.perform(post("/stages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetFilteredProjectStages() throws Exception {
        StageRolesDto stageRole = createStageRoleDto(1L, TeamRole.TESTER, 1, 1L);
        StageDto stageDto = createStageDto(1L, "Stage name", 1L, List.of(stageRole));
        List<StageDto> stages = List.of(stageDto);

        when(stageService.getFilteredProjectStages(1L, TeamRole.TESTER, null)).thenReturn(stages);

        mockMvc.perform(get("/stages/projects/1")
                        .param("teamRole", TeamRole.TESTER.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(stages.size()))
                .andExpect(jsonPath("$[0].stageId").value(stageDto.getStageId()))
                .andExpect(jsonPath("$[0].stageName").value(stageDto.getStageName()))
                .andExpect(jsonPath("$[0].projectId").value(stageDto.getProjectId()));
    }

    @Test
    void testDeleteStageDeletionTypeDeleteTasks() throws Exception {
        doNothing().when(stageService).delete(1L, DeletionType.DELETE, null);

        mockMvc.perform(delete("/stages/1")
                        .param("deletionType", DeletionType.DELETE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStageDeletionTypeCloseTasks() throws Exception {
        doNothing().when(stageService).delete(1L, DeletionType.CLOSE, null);

        mockMvc.perform(delete("/stages/1")
                        .param("deletionType", DeletionType.CLOSE.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStageDeletionTypeTransferTasks() throws Exception {
        doNothing().when(stageService).delete(1L, DeletionType.TRANSFER, 2L);

        mockMvc.perform(delete("/stages/1")
                        .param("deletionType", DeletionType.TRANSFER.toString())
                        .param("targetStageId", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStageDeletionTypeTransferTasksWithoutTargetStageId() throws Exception {
        doThrow(new InvalidStageTransferException("Target stage ID is required for task transfer"))
                .when(stageService).delete(1L, DeletionType.TRANSFER, null);

        mockMvc.perform(delete("/stages/1")
                        .param("deletionType", DeletionType.TRANSFER.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid target stage ID"))
                .andExpect(jsonPath("$.message").value("Target stage ID is required for task transfer"));
    }

    @Test
    void testUpdateStage() throws Exception {
        StageRolesDto stageRole = createStageRoleDto(1L, TeamRole.TESTER, 1, 1L);
        StageDto stageDto = createStageDto(1L, "Stage name", 1L, List.of(stageRole));

        when(stageService.update(1L, stageDto)).thenReturn(stageDto);

        mockMvc.perform(patch("/stages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageId").value(stageDto.getStageId()))
                .andExpect(jsonPath("$.stageName").value(stageDto.getStageName()))
                .andExpect(jsonPath("$.projectId").value(stageDto.getProjectId()))
                .andExpect(jsonPath("$.stageRoles").isNotEmpty());
    }

    @Test
    void testGetAllStagesByProjectId() throws Exception {
        StageDto stageDto = createStageDto(1L, "Stage name", 1L, null);
        List<StageDto> stages = List.of(stageDto);

        when(stageService.getAllStagesByProjectId(1L)).thenReturn(stages);

        mockMvc.perform(get("/stages/projects/1/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(stages.size()))
                .andExpect(jsonPath("$[0].stageId").value(stageDto.getStageId()))
                .andExpect(jsonPath("$[0].stageName").value(stageDto.getStageName()));
    }

    @Test
    void testGetStageById() throws Exception {
        StageDto stageDto = createStageDto(1L, "Stage name", 1L, null);

        when(stageService.getStageById(1L)).thenReturn(stageDto);

        mockMvc.perform(get("/stages/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageId").value(stageDto.getStageId()))
                .andExpect(jsonPath("$.stageName").value(stageDto.getStageName()))
                .andExpect(jsonPath("$.projectId").value(stageDto.getProjectId()));
    }

    private StageDto createStageDto(Long stageId, String stageName, Long projectId, List<StageRolesDto> stageRoles) {
        return new StageDto(stageId, stageName, projectId, stageRoles);
    }

    private StageRolesDto createStageRoleDto(Long id, TeamRole teamRole, Integer count, Long stageId) {
        return new StageRolesDto(id, teamRole, count, stageId);
    }
}
