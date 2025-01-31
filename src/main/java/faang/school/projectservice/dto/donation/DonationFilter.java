package faang.school.projectservice.dto.donation;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.dto.client.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DonationFilter(
        BigDecimal amountLt,
        BigDecimal amountGt,
        Currency currency,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate creationDate

) {
}
