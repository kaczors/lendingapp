package kaczorowski.lendingapp.service;

import com.google.common.base.Optional;
import kaczorowski.lendingapp.domain.ApplicationPerIpRegister;
import kaczorowski.lendingapp.repository.ApplicationPerIpRegisterRepository;
import org.joda.time.DateTime;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static kaczorowski.lendingapp.service.ApplicationPerIpRegisterService.MAXIMUM_APPLICATIONS_PER_DAY_FROM_SINGLE_IP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ApplicationPerIpRegisterServiceTest {

    private static final String SAMPLE_IP = "192.168.2.1";
    private static final DateTime SAMPLE_TIME = new DateTime("2010-06-30T01:20");

    ApplicationPerIpRegisterService applicationPerIpRegisterService;

    @Mock
    ApplicationPerIpRegisterRepository applicationPerIpRegisterRepository;

    @BeforeMethod
    private void setUp() {
        initMocks(this);
        applicationPerIpRegisterService = new ApplicationPerIpRegisterService(applicationPerIpRegisterRepository);
    }

    @Test
    public void should_throw_exception_when_daily_application_limit_per_id_exceeded() {
        //given
        given(applicationPerIpRegisterRepository.findByIpAndDate(SAMPLE_IP, SAMPLE_TIME.withTimeAtStartOfDay()))
                .willReturn(Optional.of(registerWithApplicationCount(MAXIMUM_APPLICATIONS_PER_DAY_FROM_SINGLE_IP + 1)));

        //when
        catchException(applicationPerIpRegisterService).assertApplicationCountNotExceeded(SAMPLE_IP, SAMPLE_TIME);

        //then
        assertThat(caughtException()).isInstanceOf(IllegalStateException.class);
    }

    @DataProvider(name = "overLimitVerificationTestDP")
    private Object[][] overLimitVerificationTest() {
        return new Object[][]{
                {Optional.absent()},
                {Optional.of(registerWithApplicationCount(MAXIMUM_APPLICATIONS_PER_DAY_FROM_SINGLE_IP))}
        };
    }

    @Test(dataProvider = "overLimitVerificationTestDP")
    public void should_not_throw_exception_when_application_limit_not_exceeded(Optional<ApplicationPerIpRegister> maybeApplicationRegister) {
        //given
        given(applicationPerIpRegisterRepository.findByIpAndDate(SAMPLE_IP, SAMPLE_TIME.withTimeAtStartOfDay()))
                .willReturn(maybeApplicationRegister);

        //when
        applicationPerIpRegisterService.assertApplicationCountNotExceeded(SAMPLE_IP, SAMPLE_TIME);

        //no exception expected
    }

    @Test
    public void should_register_application_if_register_entry_for_given_day_and_ip_exists() {
        //given
        final int currentApplications = 1;
        final ApplicationPerIpRegister register = registerWithApplicationCount(currentApplications);
        given(applicationPerIpRegisterRepository.findByIpAndDate(SAMPLE_IP, SAMPLE_TIME.withTimeAtStartOfDay()))
                .willReturn(Optional.of(register));

        //when
        applicationPerIpRegisterService.register(SAMPLE_IP, SAMPLE_TIME);

        //then
        assertThat(register.getApplicationCount()).isEqualTo(currentApplications + 1);
    }

    @Test
    public void should_create_new_register_entry_when_entry_for_given_day_and_ip_does_not_exists() {
        //given
        given(applicationPerIpRegisterRepository.findByIpAndDate(SAMPLE_IP, SAMPLE_TIME.withTimeAtStartOfDay()))
                .willReturn(Optional.<ApplicationPerIpRegister>absent());

        //when
        applicationPerIpRegisterService.register(SAMPLE_IP, SAMPLE_TIME);

        //then
        verify(applicationPerIpRegisterRepository).save(
                refEq(ApplicationPerIpRegister.builder()
                        .ip(SAMPLE_IP)
                        .day(SAMPLE_TIME.withTimeAtStartOfDay())
                        .applicationCount(1)
                        .build()));
    }

    private ApplicationPerIpRegister registerWithApplicationCount(int applicationCount) {
        return ApplicationPerIpRegister.builder()
                .ip(SAMPLE_IP)
                .day(SAMPLE_TIME.withTimeAtStartOfDay())
                .applicationCount(applicationCount)
                .build();
    }
}