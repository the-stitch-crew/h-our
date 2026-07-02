package stitch.crew.hour;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HourApplication {

    static void main(String[] args) {
        SpringApplication.run(HourApplication.class, args);
    }

}
