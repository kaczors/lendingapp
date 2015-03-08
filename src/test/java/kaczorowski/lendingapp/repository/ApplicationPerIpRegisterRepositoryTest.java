package kaczorowski.lendingapp.repository;

import com.google.common.base.Optional;
import kaczorowski.lendingapp.LendingApplication;
import kaczorowski.lendingapp.domain.ApplicationPerIpRegister;
import kaczorowski.lendingapp.domain.ApplicationPerIpRegisterAssert;
import org.assertj.guava.api.Assertions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;

import static kaczorowski.lendingapp.domain.ApplicationPerIpRegisterAssert.assertThat;

@SpringApplicationConfiguration(classes = LendingApplication.class)
public class ApplicationPerIpRegisterRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {
    private static final String SEARCHED_IP = "192.168.0.1";
    private static final String OTHER_IP = "192.168.1.1";
    private static final DateTime SEARCHED_DAY = DateTime.now().withTimeAtStartOfDay();
    private static final DateTime OTHER_DAY = SEARCHED_DAY.plusDays(1);

    @Autowired
    ApplicationPerIpRegisterRepository applicationPerIpRegisterRepository;

    @Test
    public void should_find_by_ip_and_day() {
        //given
        insertRegister(SEARCHED_IP, SEARCHED_DAY);
        insertRegister(SEARCHED_IP, OTHER_DAY);
        insertRegister(OTHER_IP, SEARCHED_DAY);
        insertRegister(OTHER_IP, OTHER_DAY);

        //when
        Optional<ApplicationPerIpRegister> register = applicationPerIpRegisterRepository.findByIpAndDate(SEARCHED_IP, SEARCHED_DAY);

        //then
        assertThat(register.get())
                .hasIp(SEARCHED_IP)
                .hasDay(SEARCHED_DAY);
    }

    @Test
    public void should_return_absent_response_when_no_result_found(){
        //when
        Optional<ApplicationPerIpRegister> register = applicationPerIpRegisterRepository.findByIpAndDate(SEARCHED_IP, SEARCHED_DAY);

        //then
        Assertions.assertThat(register).isAbsent();
    }

    private void insertRegister(String ip, DateTime day) {
        applicationPerIpRegisterRepository.save(
                ApplicationPerIpRegister.builder()
                        .ip(ip)
                        .day(day)
                        .build());
    }
}