package in.wynk.scheduler;

import in.wynk.scheduler.service.WynkExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "in.wynk")
public class WynkSchedulerApplication implements ApplicationRunner {

    @Autowired
    private WynkExecutorService wynkExecutorService;



    public static void main(String[] args) {
        SpringApplication.run(WynkSchedulerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("WynkSchedulerApplication Starting");
        try {
            wynkExecutorService.executeJob(args.getOptionValues("job").get(0));
        } catch (Exception ex){
            ex.printStackTrace();
            throw ex;
        }
        System.out.println("WynkSchedulerApplication Completed");
    }

}
