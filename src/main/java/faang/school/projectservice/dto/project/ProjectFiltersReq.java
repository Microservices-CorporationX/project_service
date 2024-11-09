package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;

public record ProjectFiltersReq(String name, ProjectStatus status) {
}
