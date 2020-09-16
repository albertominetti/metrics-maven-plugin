package it.minetti.demo;


import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Component
    public static class GreetingsGenerator {
        @Timed("generator")
        public String generateGreeting(String user) {
            return String.format("Hello %s", user);
        }
    }

    @RestController
    public static class DemoController {
        @Autowired
        private GreetingsGenerator greetingsGenerator;

        @Timed("controller")
        @GetMapping("/demo/greetings/{user}")
        public String greetings(String user) {
            return greetingsGenerator.generateGreeting(user);
        }
    }

}