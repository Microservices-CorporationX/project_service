package faang.school.projectservice.util;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageRolesDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;

import java.util.List;

public class StageDataUtilTest {
    public StageDto getStageDto() {
        StageRolesDto stageRole = StageRolesDto.builder().id(1L).teamRole((TeamRole.OWNER.name())).count(1).build();
        return StageDto.builder().stageId(1L).stageRoles(List.of(stageRole)).stageName("stageName").projectId(1L).userId(1L).build();
    }

    public Stage getStage() {
        StageRoles stageRole = getStageRoles();
        return Stage.builder().stageId(1L).stageRoles(List.of(stageRole)).stageName("stageName")
                .project(getProject())
                .build();
    }

    public StageRoles getStageRoles() {
        return StageRoles.builder().id(1L).teamRole(TeamRole.OWNER).count(1).build();
    }

    public Project getProject() {
        return Project.builder().id(1L).ownerId(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .name("projectName")
                .visibility(ProjectVisibility.PUBLIC).build();
    }
}


