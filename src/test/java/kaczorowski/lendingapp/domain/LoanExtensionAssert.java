package kaczorowski.lendingapp.domain;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class LoanExtensionAssert extends AssertionHelper<LoanExtensionAssert, LoanExtension> {

    public LoanExtensionAssert(LoanExtension actual) {
        super(actual, LoanExtensionAssert.class);
    }

    public static LoanExtensionAssert assertThat(LoanExtension actual) {
        return new LoanExtensionAssert(actual);
    }

    public LoanExtensionAssert hasTerm(DateTime term) {
        return failIfNotEqual("term", actual.term, term);
    }

    public LoanExtensionAssert hasAmount(BigDecimal amount) {
        return failIfNotEqual("amount", actual.amount, amount);
    }

    public LoanExtensionAssert hasDate(DateTime date) {
        return failIfNotEqual("date", actual.date, date);
    }

}
