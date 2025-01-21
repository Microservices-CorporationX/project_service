package faang.school.projectservice.service;

import faang.school.projectservice.dto.campaign.CreateCampaignDto;
import faang.school.projectservice.dto.campaign.UpdateCampaignDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.mapper.CampaignMapper;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.CampaignRepository;
import faang.school.projectservice.validator.CampaignValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CampaignServiceTest {

    @InjectMocks
    private CampaignService campaignService;

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private CampaignValidator campaignValidator;

    @Mock
    private CampaignMapper campaignMapper;

    @Mock
    private ProjectService projectService;

    private long authorId;
    private String title;
    private long projectId;
    private CreateCampaignDto createCampaignDto;
    private long campaignId;
    private UpdateCampaignDto updateCampaignDto;
    private Campaign campaign;

    @BeforeEach
    public void setUp() {
        authorId = 5L;
        title = "some title";
        projectId = 1L;
        campaignId = 10L;

        createCampaignDto = CreateCampaignDto.builder()
                .createdBy(authorId)
                .title(title)
                .projectId(projectId)
                .build();

        updateCampaignDto = UpdateCampaignDto.builder()
                .updatedBy(authorId)
                .title(title)
                .status(CampaignStatus.COMPLETED)
                .build();

        campaign = Campaign.builder()
                .createdBy(authorId)
                .title(title)
                .build();
    }

    @Test
    public void testCreateCampaign() {
        // arrange
        when(campaignMapper.toEntity(createCampaignDto))
                .thenReturn(campaign);

        // act
        campaignService.createCampaign(createCampaignDto);

        // assert
        verify(campaignRepository).save(campaign);
    }

    @Test
    public void testCreateCampaignFailsValidation() {
        // arrange
        Project project = Project.builder()
                .id(projectId)
                .build();

        when(projectService.findProjectById(projectId))
                .thenReturn(project);
        doThrow(DataValidationException.class)
                .when(campaignValidator)
                .validateAuthorRole(project, authorId);

        // act and assert
        assertThrows(DataValidationException.class,
                () -> campaignService.createCampaign(createCampaignDto));
    }

    @Test
    public void testUpdateCampaign() {
        // arrange
        when(campaignRepository.findById(campaignId))
                .thenReturn(Optional.of(campaign));

        // act
        campaignService.updateCampaign(updateCampaignDto, campaignId);

        // assert
        verify(campaignMapper).update(campaign, updateCampaignDto);
    }

    @Test
    public void testUpdateCampaignFailsValidation() {
        // arrange
        when(campaignRepository.findById(campaignId))
                .thenReturn(Optional.of(campaign));
        doThrow(DataValidationException.class)
                .when(campaignValidator)
                .validateCampaignIsNotDeleted(campaign);

        // act and assert
        assertThrows(DataValidationException.class,
                () -> campaignService.updateCampaign(updateCampaignDto, campaignId));
    }
}
