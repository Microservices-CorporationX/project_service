package ru.corporationx.projectservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.corporationx.projectservice.model.entity.Donation;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    Optional<Donation> findByIdAndUserId(Long id, Long userId);

    List<Donation> findAllByUserId(Long userId);
}
