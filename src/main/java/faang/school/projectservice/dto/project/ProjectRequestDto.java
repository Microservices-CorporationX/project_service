package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectRequestDto {
    @NotBlank
    protected String name;
    @NotBlank
    protected String description;
    @NotNull
    protected BigInteger storageSize;
    @NotNull
    protected BigInteger maxStorageSize;
    @Positive
    protected Long ownerId;
    protected Long parentProjectId;
    protected List<Long> tasksIds;
    protected List<Long> resourcesIds;
    @NotNull
    protected ProjectVisibility visibility;
    protected String coverImageId;
    protected List<Long> teamsIds;
    @Positive
    protected Long scheduleId;
    protected List<Long> stagesIds;
    protected List<Long> vacanciesIds;
    protected List<Long> momentsIds;
    protected List<Long> meetsIds;
    protected String presentationFileKey;
    protected LocalDateTime presentationGeneratedAt;
    protected List<String> galleryFileKeys;
}
