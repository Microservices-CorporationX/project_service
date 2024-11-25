package faang.school.projectservice.specification.meet;

import faang.school.projectservice.model.Meet;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MeetSpecifications {

    public static Specification<Meet> hasProjectId(Long projectId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<Meet> titleContains(String title) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Meet> meetDateAfter(LocalDateTime dateFrom) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get("meetDate"), dateFrom);
    }

    public static Specification<Meet> meetDateBefore(LocalDateTime dateTo) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("meetDate"), dateTo);
    }

    public static Specification<Meet> meetDateBetween(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("meetDate"), dateFrom, dateTo);
    }
}
