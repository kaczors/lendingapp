package kaczorowski.lendingapp.util;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//@Service
public class TimeProvider {
    public DateTime getCurrentDate() {
        return new DateTime();
    }
}
