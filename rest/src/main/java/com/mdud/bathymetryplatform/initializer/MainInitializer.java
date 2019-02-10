package com.mdud.bathymetryplatform.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

@Component
public class MainInitializer implements CommandLineRunner {

    private static List<Initializer> initializers = new ArrayList<>();

    @Override
    public void run(String... args) throws Exception {
        initializers.forEach(Initializer::init);
    }

    public static void addInitializer(Initializer initializer) {
        initializers.add(initializer);
    }


}
