package faang.school.projectservice.filters.internship;

import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class InternshipRoleFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getRolePattern() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto filters) {
        return internships.filter(internship -> internship.getInterns().stream()
                        .anyMatch(member -> member.getRoles().contains(filters.getRolePattern())))
                .peek(internship -> {
                    List<TeamMember> filteredInterns = internship.getInterns().stream()
                            .filter(member -> member.getRoles().contains(filters.getRolePattern()))
                            .toList();
                    internship.setInterns(filteredInterns);
                });
    }
}
