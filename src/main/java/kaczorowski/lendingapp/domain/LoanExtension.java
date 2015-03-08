package kaczorowski.lendingapp.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Entity
public class LoanExtension extends BaseEntity{

    @NotNull
    private BigDecimal amount;

    @NotNull
    private DateTime term;

    @NotNull
    private DateTime date;
}
