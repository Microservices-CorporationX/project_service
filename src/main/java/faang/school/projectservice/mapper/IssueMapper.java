package faang.school.projectservice.mapper;

import com.atlassian.jira.rest.client.api.domain.Issue;
import faang.school.projectservice.dto.jira.IssueDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IssueMapper {

    @Mapping(source = "dueDate", target = "deadline")
    IssueDto toIssueDto(Issue issue);

    List<IssueDto> toIssueDtos(List<Issue> issues);
}
