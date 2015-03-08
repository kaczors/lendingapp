package kaczorowski.lendingapp.service;

import kaczorowski.lendingapp.domain.Loan;
import kaczorowski.lendingapp.domain.PersonalData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static kaczorowski.lendingapp.domain.Loan.MAX_AMOUNT_STRING_REPRESENTATION;
import static lombok.AccessLevel.PRIVATE;

@Builder
@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
@Getter
public class LoanRequest {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @DecimalMax(MAX_AMOUNT_STRING_REPRESENTATION)
    @NotNull
    private BigDecimal amount;

    @NotNull
    private DateTime term;

    public Loan toLoan() {
        return Loan.builder()
                .personalData(PersonalData.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .build())
                .amount(amount)
                .term(term)
                .build();
    }
}
