package faang.school.projectservice.exseption;

public class VacancyNotFoundException extends RuntimeException {

    public VacancyNotFoundException(String message) {
        super(message);
    }
}
