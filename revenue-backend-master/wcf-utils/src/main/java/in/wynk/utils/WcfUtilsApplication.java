package in.wynk.utils;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class }, scanBasePackages = "in.wynk")
public class WcfUtilsApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(WcfUtilsApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) {
		System.out.println("WcfUtilsApplication Starting");
	}

}
