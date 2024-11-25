package faang.school.projectservice.subproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.controller.SubprojectController;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.CreateSubProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectDto.ProjectDto;
import faang.school.projectservice.dto.subprojectDto.subprojectFilterDto.SubprojectFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.filters.SubprojectFilter;
import faang.school.projectservice.service.subprojectService.SubProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {SubprojectController.class, SubProjectService.class, SubprojectFilter.class})
public class SubProjectControllerTest {
    private final String POST = "/subprojects/projects/1/subprojects";
    private final String PUT = "/subprojects/2";
    private final String GET = "/subprojects/1/getSubprojects";
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubProjectService subProjectService;

    @MockBean
    private SubprojectFilter subprojectFilter;

    @Test
    void SubProjectController_createSubProject() throws Exception {
        CreateSubProjectDto createSubProjectDto = createValidSubProjectDto();
        ProjectDto expectedProjectDto = createExpectedProjectDto();

        when(subProjectService.createSubProject(1L, createSubProjectDto)).thenReturn(expectedProjectDto);

        mockMvc.perform(post(POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(createSubProjectDto)))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedProjectDto)))
                .andExpect(status().isOk());
    }

    @Test
    void SubProjectController_createSubProject_negative() throws Exception {
        CreateSubProjectDto createSubProjectDto = createInvalidSubProjectDto();

        mockMvc.perform(post(POST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(createSubProjectDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void SubProjectController_updateSubProject() throws Exception {
        CreateSubProjectDto updateSubProjectDto = createValidSubProjectDto();
        ProjectDto expectedProjectDto = createExpectedProjectDto();

        when(subProjectService.updateSubProject(2L, updateSubProjectDto)).thenReturn(expectedProjectDto);

        mockMvc.perform(put(PUT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(updateSubProjectDto)))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(expectedProjectDto)))
                .andExpect(status().isOk());
    }

    @Test
    void SubProjectController_updateSubProject_negative() throws Exception {
        CreateSubProjectDto updateSubProjectDto = createInvalidSubProjectDto();

        mockMvc.perform(put(PUT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(updateSubProjectDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void SubProjectController_getSubProject() throws Exception {
        ProjectDto expectedProjectDto = createExpectedProjectDto();

        SubprojectFilterDto filterDto = SubprojectFilterDto.builder()
                .name("Subproject 1")
                .status(ProjectStatus.CREATED)
                .build();

        when(subProjectService.getSubProject(1L, filterDto)).thenReturn(List.of(expectedProjectDto));

        mockMvc.perform(get(GET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(filterDto)))
                .andExpect(content().json(OBJECT_MAPPER.writeValueAsString(List.of(expectedProjectDto))))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSubProject_MissingFilterDto_MissingProjectId() throws Exception {
        SubprojectFilterDto filterDto = SubprojectFilterDto.builder()
                .name("Subproject 1")
                .status(ProjectStatus.CREATED)
                .build();

        mockMvc.perform(get("//getSubprojects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filterDto)))
                .andExpect(status().isNotFound());
    }

    private CreateSubProjectDto createValidSubProjectDto() {
        return CreateSubProjectDto.builder()
                .id(2L)
                .parentID(1L)
                .name("Subproject 1")
                .description("Description of Subproject")
                .isPrivate(false)
                .children(null)
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private CreateSubProjectDto createInvalidSubProjectDto() {
        return CreateSubProjectDto.builder()
                .id(2L)
                .parentID(1L)
                .name(null)
                .description("Description of Subproject")
                .isPrivate(false)
                .children(null)
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private ProjectDto createExpectedProjectDto() {
        return ProjectDto.builder()
                .id(1L)
                .name("Subproject 1")
                .description("Description of Subproject")
                .ownerId(1L)
                .children(List.of(1L))
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }
}