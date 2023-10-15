package com.teamvoy.teamvoytestasignment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@OpenAPIDefinition(
        info = @Info(
                title = "Order service API",
                description = "Order service APIs for placing orders",
                version = "0.0.1"
        )
)
@SpringBootApplication
public class TeamvoyTestAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamvoyTestAssignmentApplication.class, args);
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(1);
    }
}
