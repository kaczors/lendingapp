package kaczorowski.lendingapp.domain;

import org.joda.time.DateTime;

public class ApplicationPerIpRegisterAssert extends AssertionHelper<ApplicationPerIpRegisterAssert, ApplicationPerIpRegister> {
    public ApplicationPerIpRegisterAssert(ApplicationPerIpRegister actual) {
        super(actual, ApplicationPerIpRegisterAssert.class);
    }

    public static ApplicationPerIpRegisterAssert assertThat(ApplicationPerIpRegister actual) {
        return new ApplicationPerIpRegisterAssert(actual);
    }

    public ApplicationPerIpRegisterAssert hasIp(String ip) {
        return failIfNotEqual("ip", actual.getIp(), ip);
    }

    public ApplicationPerIpRegisterAssert hasDay(DateTime day) {
        return failIfNotEqual("day", actual.getDay(), day);
    }
}
