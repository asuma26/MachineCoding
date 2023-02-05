package in.wynk.targeting;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "in.wynk")
public class TargetingApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(TargetingApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("TargetingApplication Starting");
    }
}