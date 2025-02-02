package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.Currency;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.exception.DataValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DonationValidatorTest {

    @InjectMocks
    private DonationValidator donationValidator;

    @Test
    void testValidateDonation() {
        DonationDto donationDto = null;

        assertThrows(DataValidationException.class,
                () -> donationValidator.validateDonation(donationDto));
    }

    @Test
    void testValidateDonationWithNullPaymentNumber() {
        DonationDto donationDto = prepareData();
        donationDto.setPaymentNumber(null);

        assertThrows(DataValidationException.class,
                () -> donationValidator.validateDonation(donationDto));
    }

    @Test
    void testValidateDonationWithNullAmount() {
        DonationDto donationDto = prepareData();
        donationDto.setAmount(null);

        assertThrows(DataValidationException.class,
                () -> donationValidator.validateDonation(donationDto));
    }

    @Test
    void testValidateDonationWithLessAmount() {
        DonationDto donationDto = prepareData();
        donationDto.setAmount(BigDecimal.valueOf(0));

        assertThrows(DataValidationException.class,
                () -> donationValidator.validateDonation(donationDto));
    }

    @Test
    void testValidateDonationWithNullCampaignId() {
        DonationDto donationDto = prepareData();
        donationDto.setCampaignId(null);

        assertThrows(DataValidationException.class,
                () -> donationValidator.validateDonation(donationDto));
    }

    @Test
    void testValidateDonationWithNullCurrency() {
        DonationDto donationDto = prepareData();
        donationDto.setCurrency(null);

        assertThrows(DataValidationException.class,
                () -> donationValidator.validateDonation(donationDto));
    }

    @Test
    void testValidateDonationWithNullUserId() {
        DonationDto donationDto = prepareData();
        donationDto.setUserId(null);

        assertThrows(DataValidationException.class,
                () -> donationValidator.validateDonation(donationDto));
    }

    private DonationDto prepareData() {
        DonationDto donationDto = new DonationDto();
        donationDto.setPaymentNumber(12345678L);
        donationDto.setAmount(BigDecimal.valueOf(2000));
        donationDto.setCampaignId(3L);
        donationDto.setCurrency(Currency.EUR);
        donationDto.setUserId(1L);
        return donationDto;
    }
}