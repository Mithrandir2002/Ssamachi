package com.earthquake.auth.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationCode(String email, String code) {
        throw new UnsupportedOperationException("TODO: implement sendVerificationCode using JavaMailSender");
    }
}
