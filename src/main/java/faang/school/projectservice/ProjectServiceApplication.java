package faang.school.projectservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class ProjectServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ProjectServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}

/*
Условия задачи

Создать глобальный обработчик исключений @RestControllerAdvice
с методами для обработки различных типов исключений в приложении.
Не забыть указать корректный ResponseStatus. Для MethodArgumentNotValidException
в ответе должны содержаться поля, которые не прошли валидацию и сообщение из аннотации в формате JSON.

Например:

{
"description": "should not be blank"
}
 */