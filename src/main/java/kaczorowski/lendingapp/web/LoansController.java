package kaczorowski.lendingapp.web;

import kaczorowski.lendingapp.domain.Loan;
import kaczorowski.lendingapp.service.LoanRequest;
import kaczorowski.lendingapp.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = LoansController.LOANS_API, produces = "application/json;charset=UTF-8")
public class LoansController {
    public static final String LOANS_API = "/loans";

    @Autowired
    LoanService loanService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> create(HttpServletRequest request, @RequestBody @Valid LoanRequest loanRequest, UriComponentsBuilder ucb) {
        Long loanId = loanService.createLoan(loanRequest, request.getRemoteAddr());
        return ResponseEntity
                .created(ucb.path(LOANS_API + "/{loanId}").buildAndExpand(loanId).toUri())
                .build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{loanId}")
    public Loan getLoan(@PathVariable Long loanId) {
        return loanService.getById(loanId);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{loanId}/extend")
    public void extend(@PathVariable Long loanId){
        loanService.extend(loanId);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Loan> getLoans(@RequestParam String firstName, @RequestParam String lastName){
        return loanService.findByClientFirstAndLastName(firstName, lastName);
    }

}
