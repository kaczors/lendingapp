package kaczorowski.lendingapp.service;


import kaczorowski.lendingapp.domain.Loan;
import kaczorowski.lendingapp.repository.LoanRepository;
import kaczorowski.lendingapp.util.TimeProvider;
import org.joda.time.DateTime;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static kaczorowski.lendingapp.domain.Loan.MAX_AMOUNT_STRING_REPRESENTATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.DateTime.parse;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class LoanServiceTest {
    private static final BigDecimal MAX_AMOUNT = new BigDecimal(MAX_AMOUNT_STRING_REPRESENTATION);
    private static final BigDecimal VALID_AMOUNT = MAX_AMOUNT.subtract(BigDecimal.ONE);
    private static final boolean SHOULD_THROW_EXCEPTION = true;
    private static final boolean SHOULD_CREATE_LOAN = false;
    private static final String IP = "192.168.0.1";
    private static final DateTime TIME_WITHOUT_AMOUNT_RESTRICTIONS = parse("2010-06-30T07:00");
    private static final DateTime NOW = TIME_WITHOUT_AMOUNT_RESTRICTIONS;
    private static final DateTime TIME_WHEN_CANT_APPLY_FOR_MAX_AMOUNT = parse("2010-06-30T05:00");
    private static final boolean APPLICATION_LIMIT_EXCEEDED = true;
    private static final boolean APPLICATION_LIMIT_NOT_EXCEEDED = false;

    private LoanService loanService;
    @Mock
    TimeProvider timeProvider;
    @Mock
    LoanRepository loanRepository;
    @Mock
    ApplicationPerIpRegisterService applicationPerIpRegisterService;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        loanService = new LoanService(loanRepository, applicationPerIpRegisterService, timeProvider);
        given(loanRepository.save(any(Loan.class))).willAnswer(returnsFirstArg());
        given(timeProvider.getCurrentDate()).willReturn(NOW);
    }

    @DataProvider(name = "maxAmountInGivenTimeDP")
    private Object[][] maxAmountInGivenTime() {
        return new Object[][]{
                {SHOULD_THROW_EXCEPTION, parse("2010-06-30T00:00"), MAX_AMOUNT},
                {SHOULD_THROW_EXCEPTION, parse("2010-06-30T05:59"), MAX_AMOUNT},
                {SHOULD_CREATE_LOAN, parse("2010-06-29T23:59"), MAX_AMOUNT},
                {SHOULD_CREATE_LOAN, parse("2010-06-29T06:00"), MAX_AMOUNT},
                {SHOULD_CREATE_LOAN, parse("2010-06-29T02:30"), VALID_AMOUNT},
                {SHOULD_CREATE_LOAN, parse("2010-06-29T02:30"), VALID_AMOUNT}};
    }

    @Test(dataProvider = "maxAmountInGivenTimeDP")
    public void should_throw_exception_when_application_for_max_amount_is_placed_between_0000_and_0600AM(boolean shouldThrowException, DateTime now, BigDecimal amount) {
        //given
        given(timeProvider.getCurrentDate()).willReturn(now);

        //when
        catchException(loanService).createLoan(createLoanRequest(amount), IP);

        //then
        if (shouldThrowException) {
            assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class);
            verifyZeroInteractions(loanRepository);
        } else {
            verify(loanRepository).save(any(Loan.class));
        }
    }

    @Test
    public void should_throw_exception_when_application_count_per_day_limit_exceeded() {
        //given
        doThrow(new IllegalStateException()).when(applicationPerIpRegisterService).assertApplicationCountNotExceeded(IP, NOW);

        //when
        catchException(loanService).createLoan(createLoanRequest(VALID_AMOUNT), IP);

        //then
        assertThat(caughtException()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void should_create_loan() {
        //given
        LoanRequest loanRequest = createLoanRequest(VALID_AMOUNT);

        //when
        loanService.createLoan(loanRequest, IP);

        //then
        verify(loanRepository).save(refEq(loanRequest.toLoan()));
    }

    @DataProvider(name = "registerInIpRegisterDP")
    private Object[][] registerInIpRegisterDP() {
        return new Object[][]{
                {VALID_AMOUNT, TIME_WITHOUT_AMOUNT_RESTRICTIONS, APPLICATION_LIMIT_NOT_EXCEEDED},
                {MAX_AMOUNT, TIME_WHEN_CANT_APPLY_FOR_MAX_AMOUNT, APPLICATION_LIMIT_NOT_EXCEEDED},
                {VALID_AMOUNT, TIME_WITHOUT_AMOUNT_RESTRICTIONS, APPLICATION_LIMIT_EXCEEDED}
        };
    }

    @Test(dataProvider = "registerInIpRegisterDP")
    public void should_register_loan_request_in_per_ip_register_regardless_of_validation_result(BigDecimal amount, DateTime now, boolean isApplicationLimitExceeded) {
        //given
        given(timeProvider.getCurrentDate()).willReturn(now);
        if (isApplicationLimitExceeded) {
            doThrow(new IllegalStateException()).when(applicationPerIpRegisterService).assertApplicationCountNotExceeded(anyString(), any(DateTime.class));
        }

        //when
        catchException(loanService).createLoan(createLoanRequest(amount), IP);

        //then
        verify(applicationPerIpRegisterService).register(IP, now);
    }

    private LoanRequest createLoanRequest(BigDecimal amount) {
        return LoanRequest.builder()
                .firstName("any firstName")
                .lastName("any lastName")
                .amount(amount)
                .term(DateTime.now())
                .build();
    }

}