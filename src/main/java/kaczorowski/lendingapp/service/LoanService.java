package kaczorowski.lendingapp.service;

import kaczorowski.lendingapp.domain.Loan;
import kaczorowski.lendingapp.repository.LoanRepository;
import kaczorowski.lendingapp.util.TimeProvider;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

import static kaczorowski.lendingapp.domain.Loan.MAX_AMOUNT_STRING_REPRESENTATION;

@Service
public class LoanService {

    final LoanRepository loanRepository;

    final ApplicationPerIpRegisterService applicationPerIpRegisterService;

    final TimeProvider timeProvider;

    @Autowired
    LoanService(LoanRepository loanRepository, ApplicationPerIpRegisterService applicationPerIpRegisterService, TimeProvider timeProvider) {
        this.loanRepository = loanRepository;
        this.timeProvider = timeProvider;
        this.applicationPerIpRegisterService = applicationPerIpRegisterService;
    }

    @Transactional
    public Long createLoan(LoanRequest loanRequest, String requesterIp) {
        DateTime now = timeProvider.getCurrentDate();
        applicationPerIpRegisterService.register(requesterIp, now);

        performRiskAnalysis(loanRequest, now, requesterIp);

        return loanRepository.save(loanRequest.toLoan()).getId();
    }

    private void performRiskAnalysis(LoanRequest loanRequest, DateTime now, String ip) {
        applicationPerIpRegisterService.assertApplicationCountNotExceeded(ip, now);
        checkForMaxAmountInForbiddenHours(loanRequest, now);
    }

    private void checkForMaxAmountInForbiddenHours(LoanRequest loanRequest, DateTime now) {
        if (maxAllowedAmount(loanRequest) && between0000and0600AM(now)) {
            throw new IllegalArgumentException("Loan application with max allowed amount cannot be placed between 00.00 and 06.00 AM.");
        }
    }

    private boolean maxAllowedAmount(LoanRequest loanRequest) {
        return loanRequest.getAmount().compareTo(new BigDecimal(MAX_AMOUNT_STRING_REPRESENTATION)) == 0;
    }

    private boolean between0000and0600AM(DateTime now) {
        int termHour = now.getHourOfDay();
        return termHour >= 0 && termHour < 6;
    }

    public Loan getById(Long loanId) {
        return loanRepository.load(loanId);
    }

    @Transactional
    public void extend(Long loanId) {
        Loan loan = loanRepository.load(loanId);
        loan.extend(timeProvider.getCurrentDate());
    }

    public List<Loan> findByClientFirstAndLastName(String firstName, String lastName) {
        return loanRepository.findByFirstAndLastName(firstName, lastName);
    }
}
