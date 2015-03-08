package kaczorowski.lendingapp.repository;

import kaczorowski.lendingapp.LendingApplication;
import kaczorowski.lendingapp.domain.Loan;
import kaczorowski.lendingapp.domain.PersonalData;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import static kaczorowski.lendingapp.domain.LoanConditions.firstName;
import static kaczorowski.lendingapp.domain.LoanConditions.lastName;
import static org.assertj.core.api.Assertions.assertThat;

@SpringApplicationConfiguration(classes = LendingApplication.class)
public class LoanRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {
    private static final String SEARCHED_FIRST_NAME = "Jan";
    private static final String SEARCHED_LAST_NAME = "Nowak";

    @Autowired
    LoanRepository loanRepository;

    @Test
    public void should_find_by_first_name_and_last_name() {
        //given
        insertLoan(SEARCHED_FIRST_NAME, SEARCHED_LAST_NAME);
        insertLoan(SEARCHED_FIRST_NAME, "other last name");
        insertLoan("other first name", SEARCHED_LAST_NAME);
        insertLoan("other first name", "other last name");

        //when
        List<Loan> loans = loanRepository.findByFirstAndLastName(SEARCHED_FIRST_NAME, SEARCHED_LAST_NAME);

        //then
        assertThat(loans)
                .isNotEmpty()
                .have(firstName(SEARCHED_FIRST_NAME))
                .have(lastName(SEARCHED_LAST_NAME));

    }

    private void insertLoan(String firstName, String lastName) {
        loanRepository.save(
                Loan.builder()
                        .personalData(PersonalData.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .build())
                        .term(DateTime.now())
                        .amount(BigDecimal.ONE)
                        .build());
    }

}