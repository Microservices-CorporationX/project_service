package faang.school.projectservice.repository;

import faang.school.projectservice.model.calendar.GoogleCalendarToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoogleCalendarTokenRepository extends JpaRepository<GoogleCalendarToken, Long> {
    GoogleCalendarToken findByUserId(long userId);
}
