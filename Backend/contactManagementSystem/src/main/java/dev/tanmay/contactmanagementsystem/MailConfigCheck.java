package dev.tanmay.contactmanagementsystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MailConfigCheck implements CommandLineRunner {

    private final Environment environment;

    public MailConfigCheck(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        System.out.println("MAIL_USERNAME env exists: "
                + (System.getenv("MAIL_USERNAME") != null));

        System.out.println("MAIL_PASSWORD env exists: "
                + (System.getenv("MAIL_PASSWORD") != null));

        System.out.println("spring.mail.username resolves: "
                + (environment.getProperty("spring.mail.username") != null));

        System.out.println("spring.mail.password resolves: "
                + (environment.getProperty("spring.mail.password") != null));
    }
}