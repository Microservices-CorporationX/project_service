package faang.school.projectservice.validator;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DonationValidator {

    public void validateDonation(DonationDto donationDto) {
        if (donationDto == null) {
            throw new DataValidationException("Донат не найден");
        }
        if (donationDto.getPaymentNumber() == null) {
            throw new DataValidationException("Номер платежа не может быть null");
        }
        if (donationDto.getAmount() == null || donationDto.getAmount().compareTo(BigDecimal.ZERO) < 1) {
            throw new DataValidationException("Сумма доната не может быть null либо меньше 1");
        }
        if (donationDto.getCampaignId() == null) {
            throw new DataValidationException("Кампания не может быть null");
        }
        if (donationDto.getCurrency() == null) {
            throw new DataValidationException("Валюта не может быть null");
        }
        if (donationDto.getUserId() == null) {
            throw new DataValidationException("Юзер доната не может быть null");
        }
    }
}
