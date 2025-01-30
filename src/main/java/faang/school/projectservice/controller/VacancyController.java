package faang.school.projectservice.controller;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.service.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VacancyController {
    public final VacancyService service;

    @PostMapping("/create_vacancy")
    public void createVacancy(@RequestBody VacancyDto vacancyDto) {
        service.createVacancy(vacancyDto);
    }

    @PutMapping("/update_vacancy")
    public void updateVacancy(@RequestBody VacancyDto vacancyDto) {
        service.updateVacancy(vacancyDto);
    }

    @DeleteMapping("/delete_vacancy/{vacancyId}")
    public void deleteVacancy(@RequestBody VacancyDto vacancyDto) {
        service.deleteVacancy(vacancyDto);
    }

}
