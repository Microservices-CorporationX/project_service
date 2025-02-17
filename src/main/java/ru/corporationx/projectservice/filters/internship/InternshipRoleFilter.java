package ru.corporationx.projectservice.filters.internship;

import org.springframework.stereotype.Component;
import ru.corporationx.projectservice.model.dto.internship.InternshipFilterDto;
import ru.corporationx.projectservice.model.entity.Internship;
import ru.corporationx.projectservice.model.entity.TeamMember;

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
