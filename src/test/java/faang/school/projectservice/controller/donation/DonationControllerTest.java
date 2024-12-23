package faang.school.projectservice.controller.donation;

import faang.school.projectservice.dto.DonationRequest;
import faang.school.projectservice.dto.FundRaisedEvent;
import faang.school.projectservice.service.donation.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DonationControllerTest {
    @InjectMocks
    private DonationController donationController;

    @Mock
    private DonationService donationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(donationController).build();
    }

    @Test
    void testDonateSuccess() throws Exception {
        DonationRequest donationRequest = new DonationRequest(456L, 150L);

        when(donationService.donate(donationRequest)).thenReturn(new FundRaisedEvent());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/donations/donate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":123, \"projectId\":456, \"amount\":150}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Donation successful"));

        verify(donationService, times(1)).donate(donationRequest);
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
