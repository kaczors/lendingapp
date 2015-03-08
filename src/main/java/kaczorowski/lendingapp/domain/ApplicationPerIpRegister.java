package kaczorowski.lendingapp.domain;

import lombok.*;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class ApplicationPerIpRegister extends BaseEntity {
    @NotNull
    String ip;

    @NotNull
    DateTime day;

    int applicationCount;

    public void register(){
        applicationCount++;
    }

    public boolean isExceed(int count){
        return applicationCount > count;
    }
}
