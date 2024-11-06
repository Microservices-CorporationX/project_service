package faang.school.projectservice.controller;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.dto.campaign.CampaignFilterDto;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/campaigns")
@Tag(name = "Project Campaigns management", description = "Operations related to manipulations with raising funds for projects")
public class CampaignController {
    private final CampaignService campaignService;
    private final CampaignMapper mapper;

    @Operation(summary = "Get a campaign", description = "Returns a campaign by a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful returns a requested campaign"),
            @ApiResponse(responseCode = "404", description = "Requested campaign wasn't found for provided ID")})
    @GetMapping("/{campaignId}")
    public CampaignDto getCampaign(@Parameter(description = "Unique ID for the campaign")
                                   @PathVariable Long campaignId) {
        Campaign requested = campaignService.findCampaignById(campaignId);
        return mapper.toDto(requested);
    }

    @Operation(summary = "Get filtered campaigns for the project",
               description = "Returns campaigns for provided project ID, based on an optional filter in reverse order by date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returns filtered campaigns, for the provided project ID"),
            @ApiResponse(responseCode = "404", description = "A project wasn't found for provided ID")})
    @PostMapping("/{projectId}")
    public List<CampaignDto> getCampaignsForProject(@Parameter(description = "Unique ID for the project")
                                                        @PathVariable Long projectId,
                                                    @Parameter(description = "Page number for partial result", required = true)
                                                        @RequestParam Integer pageNumber,
                                                    @Parameter(description = "Page size for the partial result")
                                                        @RequestParam(required = false) Integer pageSize,
                                                    @Parameter(description = "Data required for filtering the campaigns result")
                                                        @RequestBody(required = false) CampaignFilterDto filter) {
        List<Campaign> campaignsForProject = campaignService.findFilteredCampaigns(projectId, filter, pageNumber, pageSize);
        return mapper.toDtoList(campaignsForProject);
    }

    @Operation(summary = "Creates a new campaign", description = "Creates a new campaign to fund the project")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully created a new campaign a project"),
            @ApiResponse(responseCode = "400", description = """
            Can't create a new campaign. Will happen if:
            - there already exists a campaign with provided title, for the given project
            - user creating the campaign is not an owner, nor the manager for project, with provided ID"""),
            @ApiResponse(responseCode = "404", description = "A project wasn't found for provided ID")})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignDto createCampaign(@Parameter(description = "Data required to create a new campaign for the project")
                                          @RequestBody @Valid CampaignDto campaignDto) {
        Campaign toCreate = mapper.toEntity(campaignDto);
        Campaign created = campaignService.createNewCampaign(toCreate);
        return mapper.toDto(created);
    }

    @Operation(summary = "Updates info of a campaign", description = "Updates info about the campaign, title, description or both")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated info about the requested campaign"),
            @ApiResponse(responseCode = "400", description = "Both new title and description provided were null/empty"),
            @ApiResponse(responseCode = "404", description = "Requested campaign wasn't found for provided ID")})
    @PutMapping("/{campaignId}")
    public CampaignDto updateCampaignInfo(@Parameter(description = "Unique ID for the campaign")
                                              @PathVariable Long campaignId,
                                          @Parameter(description = "New title for requested campaign")
                                              @RequestParam(required = false) String title,
                                          @Parameter(description = "New description for requested campaign")
                                              @RequestParam(required = false) String description) {
        Campaign updated = campaignService.updateCampaignInfo(campaignId, title, description);
        return mapper.toDto(updated);
    }

    @Operation(summary = "Marks campaign for deletion", description = "Marks a campaign for soft deletion later, by a unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully marked for deletion, no content returned"),
            @ApiResponse(responseCode = "400", description = "Requested campaign has an ACTIVE status, illegal for deletion"),
            @ApiResponse(responseCode = "404", description = "Requested campaign wasn't found for provided ID")})
    @PatchMapping("/{campaignId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markCampaignForDeletion(@Parameter(description = "Unique ID for the campaign")
                                            @PathVariable Long campaignId) {
        campaignService.softDeleteById(campaignId);
    }
}
