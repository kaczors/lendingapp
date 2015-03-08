package kaczorowski.lendingapp.repository;

import kaczorowski.lendingapp.domain.Loan;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class LoanRepository extends EntityRepository<Loan> {
    public List<Loan> findByFirstAndLastName(String firstName, String lastName) {
        return entityManager.createQuery("from Loan where firstName=:firstName and lastName=:lastName", Loan.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .getResultList();
    }
}
