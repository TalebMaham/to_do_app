package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {

    @Value("${MAIL_PASSWORD}")
    private String mailPassword;

    public String getMailPassword() {
        return mailPassword;
    }
}

