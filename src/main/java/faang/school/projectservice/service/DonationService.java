package faang.school.projectservice.service;

import faang.school.projectservice.client.PaymentServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.dto.donation.DonationFilterDto;
import faang.school.projectservice.exception.PaymentFailedException;
import faang.school.projectservice.filter.DonationFilter;
import faang.school.projectservice.mapper.DonationMapper;
import faang.school.projectservice.model.Donation;
import faang.school.projectservice.repository.DonationRepository;
import faang.school.projectservice.validator.DonationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationService {
    private final DonationRepository donationRepository;
    private final DonationMapper donationMapper;
    private final DonationValidator donationValidator;
    private final List<DonationFilter> donationFilters;
    private final PaymentServiceClient paymentServiceClient;
    private final UserContext userContext;

    public DonationDto sendDonation(DonationDto donationDto) {
        log.info("Отправка доната: {}", donationDto);
        donationValidator.validateDonation(donationDto);
        userContext.getUserId();

        PaymentRequest paymentRequest = new PaymentRequest(
                donationDto.getPaymentNumber(),
                donationDto.getAmount(),
                donationDto.getCurrency()
        );

        try {
            log.info("Отправка платежа: {}", paymentRequest);
            PaymentResponse response = paymentServiceClient.sendPayment(paymentRequest);
            log.info("Ответ от платежного сервиса: {}", response.status());

            Donation donation = donationMapper.toEntity(donationDto);
            Donation savedDonation = donationRepository.save(donation);
            log.info("Донат сохранена: {}", savedDonation);

            return donationMapper.toDto(savedDonation);
        } catch (Exception e) {
            throw new PaymentFailedException("Ошибка при обработке платежа", e);
        }
    }

    public DonationDto getDonationByUserId(Long donationId, Long userId) {
        Donation donation = donationRepository.findByIdAndUserId(donationId, userId).get();
        return donationMapper.toDto(donation);
    }

    public List<DonationDto> getDonationsByFilter(DonationFilterDto filter, Long userId) {
        Stream<Donation> donations = donationRepository.findAllByUserId(userId).stream();

        return donationFilters.stream()
                .filter(donationFilter -> donationFilter.isApplicable(filter))
                .flatMap(donationFilter -> donationFilter.apply(donations, filter))
                .sorted(Comparator.comparing(Donation::getDonationTime))
                .map(donationMapper::toDto)
                .toList();
    }
}
