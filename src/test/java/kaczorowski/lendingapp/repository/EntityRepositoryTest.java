package kaczorowski.lendingapp.repository;

import kaczorowski.lendingapp.LendingApplication;
import kaczorowski.lendingapp.domain.Loan;
import kaczorowski.lendingapp.domain.PersonalData;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringApplicationConfiguration(classes = LendingApplication.class)
public class EntityRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {
    private static final Long NOT_EXISTING_ENTITY_ID = 1L;

    @Qualifier("loanRepository")
    @Autowired
    EntityRepository<Loan> entityRepository;

    @Test(expectedExceptions = EmptyResultDataAccessException.class)
    public void should_throw_exception_when_entity_not_found() {
        entityRepository.load(NOT_EXISTING_ENTITY_ID);
    }

    @Test
    public void should_load_entity_by_id() {
        //given
        Loan loan = entityRepository.save(Loan.builder()
                .amount(BigDecimal.ONE)
                .personalData(PersonalData.builder()
                        .firstName("firstName")
                        .lastName("lastName")
                        .build())
                .term(DateTime.now())
                .build());

        //when
        Loan retLoan = entityRepository.load(loan.getId());

        //then
        assertThat(retLoan).isEqualToComparingFieldByField(loan);
    }

}