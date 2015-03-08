package kaczorowski.lendingapp.domain;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class LoanAssert extends AssertionHelper<LoanAssert, Loan> {

    public LoanAssert(Loan actual) {
        super(actual, LoanAssert.class);
    }

    public static LoanAssert assertThat(Loan actual) {
        return new LoanAssert(actual);
    }

    public LoanAssert hasTerm(DateTime term) {
        return failIfNotEqual("term", actual.getTerm(), term);
    }

    public LoanAssert hasAmount(BigDecimal amount) {
        return failIfNotEqual("amount", actual.getAmount(), amount);
    }

}
