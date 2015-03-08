package kaczorowski.lendingapp.repository;

import com.fasterxml.jackson.databind.ser.std.IterableSerializer;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import kaczorowski.lendingapp.domain.ApplicationPerIpRegister;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;

@Repository
public class ApplicationPerIpRegisterRepository extends EntityRepository<ApplicationPerIpRegister> {

    @PersistenceContext
    EntityManager entityManager;

    public Optional<ApplicationPerIpRegister> findByIpAndDate(String ip, DateTime day) {
        List<ApplicationPerIpRegister> registers = entityManager.createQuery(
                "from ApplicationPerIpRegister where ip=:ip and day=:day", ApplicationPerIpRegister.class)
                .setParameter("ip", ip)
                .setParameter("day", day)
                .getResultList();

        return registers.isEmpty()
                ? Optional.<ApplicationPerIpRegister>absent()
                : Optional.of(getOnlyElement(registers));
    }
}
