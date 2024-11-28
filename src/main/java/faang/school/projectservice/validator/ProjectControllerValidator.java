package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class ProjectControllerValidator {

    public void validateId(Long id) {
        if(id <= 0) {
            throw new DataValidationException("Id is incorrect");
        }
    }
}
