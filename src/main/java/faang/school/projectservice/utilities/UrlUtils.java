package faang.school.projectservice.utilities;

import lombok.experimental.UtilityClass;

/**
 * Класс содержит константы для формирования url.
 *
 */
@UtilityClass
public class UrlUtils {
    public static final String MAIN_URL = "/api/project-service";
    public static final String V1 = "/v1";
    public static final String PROJECTS = "/projects";
    public static final String FILTER = "/filter";
    public static final String ID = "/{id}";
}
