package kaczorowski.lendingapp;


import kaczorowski.lendingapp.util.TimeProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LendingApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LendingApplication.class, args);
    }

    @Bean
    TimeProvider timeProvider(){
        return new TimeProvider();
    }

}
