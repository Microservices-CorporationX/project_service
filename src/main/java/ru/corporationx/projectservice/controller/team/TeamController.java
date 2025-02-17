package ru.corporationx.projectservice.controller.team;

import ru.corporationx.projectservice.model.dto.team.TeamDto;
import ru.corporationx.projectservice.service.team.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public TeamDto createTeam(@RequestBody TeamDto teamDto) {
        return teamService.createTeam(teamDto);
    }

    @GetMapping
    public List<TeamDto> getTeams() {
        return teamService.getTeams();
    }

    @GetMapping("/{teamId}")
    public TeamDto getTeam(@PathVariable @Valid Long teamId) {
        return teamService.getTeam(teamId);
    }
}
