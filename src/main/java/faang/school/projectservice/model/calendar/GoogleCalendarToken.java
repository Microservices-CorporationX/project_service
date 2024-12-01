package faang.school.projectservice.model.calendar;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "google_calendar_token")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GoogleCalendarToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token", nullable = false)
    private String token;
}
