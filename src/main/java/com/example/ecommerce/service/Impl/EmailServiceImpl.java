package com.example.ecommerce.service.Impl;

import com.example.ecommerce.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String toEmail, String verificationCode) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Email Verification - Ecommerce App");

            String emailContent = """
                    <html>
                        <body>
                            <h2>Email Verification</h2>
                            <p>Thank you for registering.</p>
                            <p>Your verification code is:</p>
                            <h3 style="color:blue;">%s</h3>
                            <p>This code is valid for 10 minutes.</p>
                            <br>
                            <p>If you did not request this, please ignore this email.</p>
                        </body>
                    </html>
                    """.formatted(verificationCode);

            helper.setText(emailContent, true); // true = HTML

            mailSender.send(message);

            log.info("Verification email sent to {}", toEmail);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}

