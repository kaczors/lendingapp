package kaczorowski.lendingapp;

import kaczorowski.lendingapp.util.TimeProvider;
import org.joda.time.DateTime;

public class TestTimeProvider extends TimeProvider {
    public static final String DEFAULT_TIME = "2013-06-2T13:40";

    private DateTime dateTime;

    public TestTimeProvider() {
        dateTime = new DateTime(DEFAULT_TIME);
    }

    public void setTime(String time) {
        dateTime = new DateTime(time);

    }

    @Override
    public DateTime getCurrentDate() {
        return dateTime;
    }
}
