package rs.ac.uns.ftn.sep.bitcoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "rs.ac.uns.ftn.sep")
@EnableScheduling
public class BitcoinApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitcoinApplication.class, args);
    }

}
