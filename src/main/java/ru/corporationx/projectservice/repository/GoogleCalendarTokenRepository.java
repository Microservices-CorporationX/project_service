package ru.corporationx.projectservice.repository;

import ru.corporationx.projectservice.model.entity.calendar.GoogleCalendarToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoogleCalendarTokenRepository extends JpaRepository<GoogleCalendarToken, Long> {
    GoogleCalendarToken findByUserId(long userId);
}
