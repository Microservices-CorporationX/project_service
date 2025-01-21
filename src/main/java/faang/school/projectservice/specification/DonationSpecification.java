package faang.school.projectservice.specification;

import faang.school.projectservice.model.Donation;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DonationSpecification {

    public Specification<Donation> getSpecByExampleWithAmountRange(Example<Donation> example) {
        Donation donationProbe = example.getProbe();
        BigDecimal minAmount = donationProbe.getMinDonationAmount();
        BigDecimal maxAmount = donationProbe.getMaxDonationAmount();

        Specification<Donation> amountRangeSpec = getAmountRangeSpec(minAmount, maxAmount);

        Specification<Donation> exampleSpec = (root, query, criteriaBuilder) ->
                QueryByExamplePredicateBuilder.getPredicate(root, criteriaBuilder, example);

        return Specification.where(exampleSpec).and(amountRangeSpec);
    }

    private Specification<Donation> getAmountRangeSpec(BigDecimal minAmount, BigDecimal maxAmount) {
        return (root, query, criteriaBuilder) -> {
            if (minAmount != null && maxAmount != null) {
                return criteriaBuilder.between(root.get("amount"), minAmount, maxAmount);
            } else if (minAmount != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount);
            } else if (maxAmount != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount);
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }
}
