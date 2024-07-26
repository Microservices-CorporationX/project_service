package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
@Validated
public class MomentController {

    private final MomentService momentService;
    private final UserContext userContext;

    @GetMapping("/{id}")
    public MomentResponseDto getById(@Positive @PathVariable long id) {
        return momentService.getById(id);
    }

    @GetMapping
    public List<MomentResponseDto> getAll() {
        return momentService.getAll();
    }

    @GetMapping("/project/{projectId}")
    public List<MomentResponseDto> getAllFilteredByProjectId(@PathVariable @Positive Long projectId,
                                          @RequestBody(required = false) @Validated MomentFilterDto momentFilterDto) {

        return momentService.getAllFilteredByProjectId(projectId, momentFilterDto);
    }

    @PostMapping
    public MomentResponseDto addNew(@RequestBody @Validated MomentRequestDto momentRequestDto) {

        validateProjectsAndMembers(momentRequestDto);
        return momentService.addNew(momentRequestDto, userContext.getUserId());
    }

    @PatchMapping
    public MomentResponseDto update(@RequestBody @Validated MomentUpdateDto momentUpdateDto) {

        return momentService.update(momentUpdateDto, userContext.getUserId());
    }


    private void validateProjectsAndMembers(MomentRequestDto momentRequestDto) {
        if (momentRequestDto.getProjectIds() == null
                && momentRequestDto.getTeamMemberIds() == null) {

            throw new DataValidationException(ErrorMessage.MOMENT_PROJECTS_AND_MEMBERS_NULL);
        }
    }
}
