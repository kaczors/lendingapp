package kaczorowski.lendingapp.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Condition;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanConditions {

    public static Condition<Loan> firstName(final String firstName) {
        return new Condition<Loan>() {
            @Override
            public boolean matches(Loan loan) {
                return Objects.equals(firstName, loan.getFirstName());
            }
        };
    }

    public static Condition<Loan> lastName(final String lastName) {
        return new Condition<Loan>() {
            @Override
            public boolean matches(Loan loan) {
                return Objects.equals(lastName, loan.getLastName());
            }
        };
    }

}
