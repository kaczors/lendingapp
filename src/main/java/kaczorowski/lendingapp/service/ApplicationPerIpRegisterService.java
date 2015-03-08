package kaczorowski.lendingapp.service;

import com.google.common.base.Optional;
import kaczorowski.lendingapp.domain.ApplicationPerIpRegister;
import kaczorowski.lendingapp.repository.ApplicationPerIpRegisterRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationPerIpRegisterService {
    static final int MAXIMUM_APPLICATIONS_PER_DAY_FROM_SINGLE_IP = 3;
    private final ApplicationPerIpRegisterRepository applicationPerIpRegisterRepository;

    @Autowired
    ApplicationPerIpRegisterService(ApplicationPerIpRegisterRepository applicationPerIpRegisterRepository) {
        this.applicationPerIpRegisterRepository = applicationPerIpRegisterRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void register(String ip, DateTime now) {
        Optional<ApplicationPerIpRegister> maybeApplicationRegister = applicationPerIpRegisterRepository.findByIpAndDate(ip, now.withTimeAtStartOfDay());
        if (maybeApplicationRegister.isPresent()) {
            maybeApplicationRegister.get().register();
        } else {
            applicationPerIpRegisterRepository.save(
                    ApplicationPerIpRegister.builder()
                            .applicationCount(1)
                            .day(now.withTimeAtStartOfDay())
                            .ip(ip)
                            .build());
        }
    }

    public void assertApplicationCountNotExceeded(String ip, DateTime now) {
        Optional<ApplicationPerIpRegister> maybeApplicationRegister = applicationPerIpRegisterRepository.findByIpAndDate(ip, now.withTimeAtStartOfDay());
        if (maybeApplicationRegister.isPresent() && maybeApplicationRegister.get().isExceed(MAXIMUM_APPLICATIONS_PER_DAY_FROM_SINGLE_IP)) {
            throw new IllegalStateException("Exceeded maximum number of applications per day.");
        }
    }
}
