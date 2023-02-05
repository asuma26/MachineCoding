package in.wynk.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "in.wynk")
@Slf4j
public class OrderApiTestApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(OrderApiTestApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("OrderApiApplicationTests starting...");
    }

}
