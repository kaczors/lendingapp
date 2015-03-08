package kaczorowski.lendingapp.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static java.math.BigDecimal.valueOf;
import static lombok.AccessLevel.PRIVATE;

@JsonAutoDetect(fieldVisibility = ANY)
@Entity
@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class Loan extends BaseEntity {
    public static final String MAX_AMOUNT_STRING_REPRESENTATION = "10000";

    @JsonUnwrapped
    @Embedded
    PersonalData personalData;

    @DecimalMax(MAX_AMOUNT_STRING_REPRESENTATION)
    @NotNull
    BigDecimal amount;

    @NotNull
    DateTime term;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    List<LoanExtension> extensions = new ArrayList<>();

    public void extend(DateTime now) {
        amount = amount.multiply(valueOf(1.5));
        term = term.plusWeeks(1);
        extensions.add(new LoanExtension(amount, term, now));
    }
}
