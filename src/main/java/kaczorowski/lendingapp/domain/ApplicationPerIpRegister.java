package kaczorowski.lendingapp.domain;

import lombok.*;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
public class ApplicationPerIpRegister extends BaseEntity {
    @NotNull
    private String ip;

    @NotNull
    private DateTime day;

    private int applicationCount;

    public void register(){
        applicationCount++;
    }
}
