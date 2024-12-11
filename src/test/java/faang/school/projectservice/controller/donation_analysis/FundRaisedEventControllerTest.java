package faang.school.projectservice.controller.donation_analysis;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.donation_analysis.FundRaisedEventDto;
import faang.school.projectservice.handler.GlobalExceptionHandler;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.service.donation_analysis.FundRaisedEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@WebMvcTest
@ContextConfiguration(classes = {FundRaisedEventController.class})
public class FundRaisedEventControllerTest {

    private final static String URL = "/api/v1/project-service";

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FundRaisedEventService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void test() throws Exception {
        Donation donation = Donation.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(1))
                .donationTime(LocalDateTime.now())
                .build();

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(donation)))
                .andExpect(status().isOk());
    }
}
