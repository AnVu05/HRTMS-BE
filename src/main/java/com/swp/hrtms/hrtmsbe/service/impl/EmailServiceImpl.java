package com.swp.hrtms.hrtmsbe.service.impl;

// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.otp.mock:true}")
    private boolean otpMock;

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

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
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(to);
                helper.setSubject(subject);

                String htmlContent = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; border-radius: 8px; overflow: hidden;\">"
                        + "<div style=\"background-color: #0f172a; padding: 20px; text-align: center;\">"
                        + "<h1 style=\"color: #ffffff; margin: 0; font-size: 24px;\">HRTMS</h1>"
                        + "</div>"
                        + "<div style=\"padding: 30px; background-color: #ffffff;\">"
                        + "<h2 style=\"color: #1e293b; margin-top: 0;\">Login Verification</h2>"
                        + "<p style=\"color: #475569; font-size: 16px; line-height: 1.5;\">You recently requested to sign in to your HRTMS portal account. Please use the verification code below to complete your login:</p>"
                        + "<div style=\"background-color: #f1f5f9; padding: 20px; text-align: center; border-radius: 6px; margin: 25px 0;\">"
                        + "<span style=\"font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #0f172a;\">"
                        + otp + "</span>"
                        + "</div>"
                        + "<p style=\"color: #475569; font-size: 14px; margin-bottom: 0;\">This code will expire in <strong>5 minutes</strong>. If you did not request this code, please ignore this email.</p>"
                        + "</div>"
                        + "<div style=\"background-color: #f8fafc; padding: 15px; text-align: center; border-top: 1px solid #e5e7eb;\">"
                        + "<p style=\"color: #94a3b8; font-size: 12px; margin: 0;\">&copy; 2026 HRTMS - Elite Racing Management. All rights reserved.</p>"
                        + "</div>"
                        + "</div>";

                helper.setText(htmlContent, true); // true indicates it's HTML

                mailSender.send(message);
                log.info("Real HTML email successfully sent to {}", to);
            } catch (Exception e) {
                log.error("Failed to send email to {}", to, e);
            }
        }
    }
}
