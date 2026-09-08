package com.earthquake.auth.service;

public interface MailService {

    void sendVerificationCode(String email, String code);
}
