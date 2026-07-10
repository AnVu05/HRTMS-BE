package com.swp.hrtms.hrtmsbe.service.impl;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.otp.mock:true}")
    private boolean otpMock;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Override
    public void sendOtp(String to, String otp) {
        String subject = "HRTMS Secure Access Code (OTP)";

        // Luôn ghi log ra console để thuận tiện kiểm thử
        log.info("========================================");
        log.info("[MOCK EMAIL] To: {}", to);
        log.info("[MOCK EMAIL] Subject: {}", subject);
        log.info("[MOCK EMAIL] OTP Code: {}", otp);
        log.info("========================================");

        // Rewrite for authentication & authorization: Real email configuration
        if (!otpMock) {
            if (mailSender == null) {
                log.error("JavaMailSender is not initialized. Please check your application.yaml mail settings.");
                return;
            }
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(to);
                message.setSubject(subject);
                message.setText("Welcome to HRTMS!\nYour secure registration OTP verification code is: " + otp
                        + "\nIt will expire in 5 minutes.");
                mailSender.send(message);
                log.info("[EMAIL SYSTEM] Real email successfully sent to {}", to);
            } catch (Exception e) {
                log.error("[EMAIL SYSTEM] Error sending email to " + to, e);
            }
        }
    }
}
