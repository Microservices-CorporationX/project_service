package faang.school.projectservice.controller;

import faang.school.projectservice.dto.CandidateAddDto;
import faang.school.projectservice.dto.vacancy.FilterVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vacancy")
public class VacancyController {
    private final VacancyService vacancyService;
    private final VacancyMapper vacancyMapper;

    @PostMapping("/{currentUserId}")
    public ResponseEntity<Void> createVacancy(@RequestBody VacancyCreateDto vacancyCreateDto, @PathVariable Long currentUserId) {
        Vacancy vacancy = vacancyMapper.toEntity(vacancyCreateDto);
        vacancyService.createVacancy(vacancy, currentUserId,vacancyCreateDto.getProjectId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{vacancyId}/{currentUserId}")
    public ResponseEntity<Void> updateVacancy(@RequestBody VacancyUpdateDto vacancyUpdateDto,
                              @PathVariable Long currentUserId, @PathVariable Long vacancyId) {
        Vacancy vacancy = vacancyMapper.toEntity(vacancyUpdateDto);
        vacancyService.updateVacancy(vacancy, vacancyId, currentUserId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{vacancyId}/candidates/{currentUserId}")
    public ResponseEntity<Void> addCandidate(@RequestBody CandidateAddDto candidateAddDto,
                             @PathVariable Long vacancyId, @PathVariable Long currentUserId) {
        vacancyService.addCandidatesToVacancy(candidateAddDto.getCandidatesIds(),
                candidateAddDto.getProjectId(), vacancyId, currentUserId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{vacancyId}/{currentUserId}")
    public ResponseEntity<Void> removeVacancy(@PathVariable Long vacancyId, @PathVariable Long currentUserId) {
        vacancyService.removeVacancy(vacancyId, currentUserId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<VacancyDto>> getFilteredByNameAndPositionVacancies(@RequestBody FilterVacancyDto filterVacancyDto) {
        List<Vacancy> vacancies = vacancyService.filterVacancies(filterVacancyDto.getPosition(),
                filterVacancyDto.getVacancyName());
        return ResponseEntity.ok(vacancyMapper.toDto(vacancies));
    }

    @GetMapping("/{vacancyId}")
    public ResponseEntity<VacancyDto> findVacancyById(@PathVariable Long vacancyId) {
        Vacancy vacancy = vacancyService.getVacancyById(vacancyId);
        return ResponseEntity.ok(vacancyMapper.toDto(vacancy));
    }
}
