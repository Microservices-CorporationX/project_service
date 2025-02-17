package ru.corporationx.projectservice.model.entity.calendar;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "google_calendar_token")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class GoogleCalendarToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;
}
