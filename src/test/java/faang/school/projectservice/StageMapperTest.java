package faang.school.projectservice;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageUpdateDto;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.TestCase.assertNotNull;

@SpringBootTest
@ContextConfiguration
public class StageMapperTest {

    @Autowired
    private StageMapper stageMapper;

    @Test
    public void testToStage() {
        StageDto stageDto = new StageDto();
        Stage stage = stageMapper.toStage(stageDto);
        assertNotNull(stage);
    }

    @Test
    public void testToStageDto() {
        Stage stage = new Stage();
        StageDto stageDto = stageMapper.toStageDto(stage);
        assertNotNull(stageDto);
    }

    @Test
    public void testToStageUpdateDto() {
        StageDto stageDto = new StageDto();
        StageUpdateDto stageUpdateDto = stageMapper.toStageUpdateDto(stageDto);
        assertNotNull(stageUpdateDto);
    }

    @Test
    public void testToStageDtoFromUpdateDto() {
        StageUpdateDto stageUpdateDto = new StageUpdateDto();
        StageDto stageDto = stageMapper.toStageDto(stageUpdateDto);
        assertNotNull(stageDto);
    }
}