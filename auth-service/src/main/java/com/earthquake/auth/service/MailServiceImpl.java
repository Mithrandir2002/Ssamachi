package com.earthquake.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public MailServiceImpl(JavaMailSender mailSender,
                           @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendVerificationCode(String email, String code) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(email);
            helper.setSubject("Mã xác thực đăng ký - Earthquake Platform");
            helper.setText(buildHtmlBody(code), true);
        } catch (MessagingException e) {
            throw new MailSendException("Could not build verification email for " + email, e);
        }


        mailSender.send(message);
        log.info("Verification code sent to {}", email);
    }

    private String buildHtmlBody(String code) {
        return """
                <div style="font-family: Arial, sans-serif; font-size: 15px; color: #222;">
                  <h2 style="margin-bottom: 8px;">Xác thực đăng ký</h2>
                  <p>Mã xác thực của bạn là:</p>
                  <p style="font-size: 30px; font-weight: bold; letter-spacing: 6px; margin: 16px 0;">%s</p>
                  <p>Mã có hiệu lực trong 5 phút.</p>
                  <p style="color: #777; font-size: 13px;">Nếu bạn không yêu cầu đăng ký, hãy bỏ qua email này.</p>
                </div>
                """.formatted(code);
    }
}
