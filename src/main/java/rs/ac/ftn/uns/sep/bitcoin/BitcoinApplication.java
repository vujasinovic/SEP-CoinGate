package rs.ac.ftn.uns.sep.bitcoin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import rs.ac.ftn.uns.sep.bitcoin.properties.BitcoinProperties;

@SpringBootApplication
@EnableConfigurationProperties(BitcoinProperties.class)
public class BitcoinApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitcoinApplication.class, args);
    }

}
