package faang.school.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.vacation.FilterVacancyDto;
import faang.school.projectservice.dto.vacation.VacancyDto;
import faang.school.projectservice.service.VacationService;
import faang.school.projectservice.utilities.UrlUtils;
import faang.school.projectservice.validation.ValidationVacancies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import utils.VacationDataCreatorTest;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test class for VacancyControllerTest")
class VacancyControllerTest {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private VacationService vacationService;
    @Mock
    private ValidationVacancies validationVacancies;
    @InjectMocks
    private VacancyController vacancyController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(vacancyController).build();
    }

    @Test
    void testSaveSuccess() throws Exception {
        VacancyDto vacancyDtoInput = VacationDataCreatorTest.getSaveInputVacancyDto();
        VacancyDto vacancyDtoOutput = VacationDataCreatorTest.getSaveOutputVacancyDto(1);

        when(vacationService.saveVacation(vacancyDtoInput)).
                thenReturn(vacancyDtoOutput);

        mockMvc.perform(MockMvcRequestBuilders.
                        post(UrlUtils.MAIN_URL + UrlUtils.VACANCY + UrlUtils.V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyDtoInput)))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(vacancyDtoOutput.id()))
                .andExpect(jsonPath("$.name").value(vacancyDtoOutput.name()))
                .andExpect(jsonPath("$.description").value(vacancyDtoOutput.description()))
                .andExpect(jsonPath("$.projectId").value(vacancyDtoOutput.projectId()))
                .andExpect(jsonPath("$.status").value(vacancyDtoOutput.status().name()))
                .andExpect(jsonPath("$.createdBy").value(vacancyDtoOutput.createdBy()))
                .andExpect(jsonPath("$.salary").value(vacancyDtoOutput.salary()))
                .andExpect(jsonPath("$.count").value(vacancyDtoOutput.count()))
                .andExpect(jsonPath("$.workSchedule").value(vacancyDtoOutput.workSchedule().name()))
                .andExpect(status().isOk());
        verify(vacationService, times(1)).saveVacation(vacancyDtoInput);
    }

    @Test
    void testSaveNullInputDataFail() throws Exception {
        VacancyDto vacancyDtoInput = null;

        mockMvc.perform(MockMvcRequestBuilders.
                        post(UrlUtils.MAIN_URL + UrlUtils.VACANCY + UrlUtils.V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyDtoInput)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(vacationService, times(0)).saveVacation(vacancyDtoInput);
    }

    @Test
    void testUpdateSuccess() throws Exception {
        VacancyDto vacancyDtoInput = VacationDataCreatorTest.getSaveOutputVacancyDto(1);
        VacancyDto vacancyDtoOutput = VacationDataCreatorTest.getSaveOutputVacancyDto(1);

        when(vacationService.updateVacation(vacancyDtoInput)).
                thenReturn(vacancyDtoOutput);

        mockMvc.perform(MockMvcRequestBuilders.
                        patch(UrlUtils.MAIN_URL + UrlUtils.VACANCY + UrlUtils.V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyDtoInput)))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(vacancyDtoOutput.id()))
                .andExpect(jsonPath("$.name").value(vacancyDtoOutput.name()))
                .andExpect(jsonPath("$.description").value(vacancyDtoOutput.description()))
                .andExpect(jsonPath("$.projectId").value(vacancyDtoOutput.projectId()))
                .andExpect(jsonPath("$.status").value(vacancyDtoOutput.status().name()))
                .andExpect(jsonPath("$.createdBy").value(vacancyDtoOutput.createdBy()))
                .andExpect(jsonPath("$.salary").value(vacancyDtoOutput.salary()))
                .andExpect(jsonPath("$.count").value(vacancyDtoOutput.count()))
                .andExpect(jsonPath("$.workSchedule").value(vacancyDtoOutput.workSchedule().name()))
                .andExpect(status().isOk());
        verify(vacationService, times(1)).updateVacation(vacancyDtoInput);
    }

    @Test
    void testUpdateNullInputDataFail() throws Exception {
        VacancyDto vacancyDtoInput = null;

        mockMvc.perform(MockMvcRequestBuilders.
                        patch(UrlUtils.MAIN_URL + UrlUtils.VACANCY + UrlUtils.V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyDtoInput)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(vacationService, times(0)).updateVacation(vacancyDtoInput);
    }

    @Test
    void testDeleteSuccess() throws Exception {
        VacancyDto vacancyDtoInput = VacationDataCreatorTest.getDeleteInputVacancyDto();
        VacancyDto vacancyDtoOutput = VacationDataCreatorTest.getSaveOutputVacancyDto(1);

        when(vacationService.deleteVacation(vacancyDtoInput.id())).
                thenReturn(vacancyDtoOutput);

        mockMvc.perform(MockMvcRequestBuilders.
                        delete(UrlUtils.MAIN_URL + UrlUtils.VACANCY + UrlUtils.V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyDtoInput)))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(vacancyDtoOutput.id()))
                .andExpect(jsonPath("$.name").value(vacancyDtoOutput.name()))
                .andExpect(jsonPath("$.description").value(vacancyDtoOutput.description()))
                .andExpect(jsonPath("$.projectId").value(vacancyDtoOutput.projectId()))
                .andExpect(jsonPath("$.status").value(vacancyDtoOutput.status().name()))
                .andExpect(jsonPath("$.createdBy").value(vacancyDtoOutput.createdBy()))
                .andExpect(jsonPath("$.salary").value(vacancyDtoOutput.salary()))
                .andExpect(jsonPath("$.count").value(vacancyDtoOutput.count()))
                .andExpect(jsonPath("$.workSchedule").value(vacancyDtoOutput.workSchedule().name()))
                .andExpect(status().isOk());
        verify(vacationService, times(1)).deleteVacation(vacancyDtoInput.id());
        verify(validationVacancies, times(1)).isVacancyExist(vacancyDtoInput.id());
    }

    @Test
    void testDeleteNullInputDataFail() throws Exception {
        VacancyDto vacancyDtoInput = null;

        mockMvc.perform(MockMvcRequestBuilders.
                        delete(UrlUtils.MAIN_URL + UrlUtils.VACANCY + UrlUtils.V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyDtoInput)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetVacancyByIdSuccess() throws Exception {
        VacancyDto vacancyDtoInput = VacationDataCreatorTest.getDeleteInputVacancyDto();
        VacancyDto vacancyDtoOutput = VacationDataCreatorTest.getSaveOutputVacancyDto(1);

        when(vacationService.getVacancyById(vacancyDtoInput.id())).
                thenReturn(vacancyDtoOutput);

        mockMvc.perform(MockMvcRequestBuilders.
                        post(UrlUtils.MAIN_URL + UrlUtils.VACANCY + UrlUtils.V1 + UrlUtils.ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyDtoInput)))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(vacancyDtoOutput.id()))
                .andExpect(jsonPath("$.name").value(vacancyDtoOutput.name()))
                .andExpect(jsonPath("$.description").value(vacancyDtoOutput.description()))
                .andExpect(jsonPath("$.projectId").value(vacancyDtoOutput.projectId()))
                .andExpect(jsonPath("$.status").value(vacancyDtoOutput.status().name()))
                .andExpect(jsonPath("$.createdBy").value(vacancyDtoOutput.createdBy()))
                .andExpect(jsonPath("$.salary").value(vacancyDtoOutput.salary()))
                .andExpect(jsonPath("$.count").value(vacancyDtoOutput.count()))
                .andExpect(jsonPath("$.workSchedule").value(vacancyDtoOutput.workSchedule().name()))
                .andExpect(status().isOk());
        verify(vacationService, times(1)).getVacancyById(vacancyDtoInput.id());
    }

    @Test
    void testGetVacancyByIdNullInputDataFail() throws Exception {
        VacancyDto vacancyDtoInput = null;

        mockMvc.perform(MockMvcRequestBuilders.
                        post(UrlUtils.MAIN_URL + UrlUtils.VACANCY + UrlUtils.V1 + UrlUtils.ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vacancyDtoInput)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFilterSuccess() throws Exception {
        List<VacancyDto> vacancyDtoList = VacationDataCreatorTest.getListVacancyDto(3);
        FilterVacancyDto filterVacancyDto = VacationDataCreatorTest.getFilter();

        when(vacationService.findByFilter(filterVacancyDto)).
                thenReturn(vacancyDtoList);

        mockMvc.perform(MockMvcRequestBuilders.
                        post(UrlUtils.MAIN_URL + UrlUtils.VACANCY + UrlUtils.V1 + UrlUtils.VACANCY_FILTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterVacancyDto)))
                .andDo(print())
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$.[0].id").value(vacancyDtoList.get(0).id()))
                .andExpect(jsonPath("$.[1].id").value(vacancyDtoList.get(1).id()))
                .andExpect(jsonPath("$.[2].id").value(vacancyDtoList.get(2).id()))
                .andExpect(status().isOk());
        verify(vacationService, times(1)).findByFilter(filterVacancyDto);
    }

    @Test
    void testFilterNullOutputSuccess() throws Exception {
        List<VacancyDto> vacancyDtoList = null;
        FilterVacancyDto filterVacancyDto = VacationDataCreatorTest.getFilter();

        when(vacationService.findByFilter(filterVacancyDto)).
                thenReturn(vacancyDtoList);

        mockMvc.perform(MockMvcRequestBuilders.
                        post(UrlUtils.MAIN_URL + UrlUtils.VACANCY + UrlUtils.V1 + UrlUtils.VACANCY_FILTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterVacancyDto)))
                .andDo(print())
                .andExpect(content().string(""))
                .andExpect(status().isOk());
        verify(vacationService, times(1)).findByFilter(filterVacancyDto);
    }
}