package kaczorowski.lendingapp.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.math.BigDecimal.valueOf;
import static org.joda.time.DateTime.parse;

public class LoanTest {

    @DataProvider(name = "extendDP")
    private Object[][] extendDP() {
        return new Object[][]{
                {createLoan(parse("2005-02-12"), valueOf(7)), parse("2005-02-19"), valueOf(10.5)},
                {createLoan(parse("2015-03-29"), valueOf(325.30)), parse("2015-04-05"), valueOf(487.95)},
                {createLoan(parse("2014-12-30"), valueOf(1)), parse("2015-01-06"), valueOf(1.5)}};
    }

    @Test(dataProvider = "extendDP")
    public void should_extend_loan(Loan toBeExtended, DateTime expectedTerm, BigDecimal expectedAmount) {
        //given
        DateTime now = DateTime.now();

        //when
        toBeExtended.extend(now);

        //then
        LoanAssert.assertThat(toBeExtended)
                .hasTerm(expectedTerm)
                .hasAmount(expectedAmount);

        LoanExtensionAssert.assertThat(getOnlyElement(toBeExtended.extensions))
                .hasAmount(expectedAmount)
                .hasTerm(expectedTerm)
                .hasDate(now);
    }

    private Loan createLoan(DateTime term, BigDecimal amount) {
        return Loan.builder()
                .term(term)
                .amount(amount)
                .extensions(Lists.<LoanExtension>newArrayList())
                .build();
    }

}