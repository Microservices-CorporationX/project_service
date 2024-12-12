package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.FundRaisedEvent;
import faang.school.projectservice.publisher.FundRaisedEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DonationControllerTest {
    @InjectMocks
    private DonationController donationController;

    @Mock
    private FundRaisedEventPublisher fundRaisedEventPublisher;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(donationController).build();
    }

    @Test
    void testDonateSuccess() throws Exception {
        doNothing().when(fundRaisedEventPublisher).publish(any(FundRaisedEvent.class));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/donations/donate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1, \"projectId\":100, \"amount\":500}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Donation successful"));

        verify(fundRaisedEventPublisher, times(1)).publish(any(FundRaisedEvent.class));
    }

    @Test
    void testDonateWithBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/donation/donate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1,\"projectId\":100}"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
