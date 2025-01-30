package faang.school.projectservice.model;

import java.util.List;

public enum VacancyStatus {
    OPEN, CLOSED, POSTPONED;
    public static List<VacancyStatus> getAll() {
        return List.of(VacancyStatus.values());
    }
}
