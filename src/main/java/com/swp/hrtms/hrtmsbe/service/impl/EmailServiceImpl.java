package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${app.otp.mock:true}")
    private boolean otpMock;

    @Override
    public void sendOtp(String to, String otp) {
        String subject = "HRTMS Secure Access Code (OTP)";

        // Luôn ghi log ra console để thuận tiện kiểm thử
        log.info("========================================");
        log.info("[MOCK EMAIL] To: {}", to);
        log.info("[MOCK EMAIL] Subject: {}", subject);
        log.info("[MOCK EMAIL] OTP Code: {}", otp);
        log.info("========================================");

        if (!otpMock) {
            log.warn(
                    "Real email sending is disabled because spring-boot-starter-mail is not included. Please add spring-boot-starter-mail dependency to pom.xml.");
        }
    }
}


