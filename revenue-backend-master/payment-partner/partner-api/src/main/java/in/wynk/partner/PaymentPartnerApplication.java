package in.wynk.partner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "in.wynk")
public class PaymentPartnerApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(PaymentPartnerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("PaymentPartnerApplication Starting");
    }

}
