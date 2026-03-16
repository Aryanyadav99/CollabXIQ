package com.collabskill.collabxskill.Service.ServiceImpl;

import com.collabskill.collabxskill.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOtp(String email, String otp) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Your OTP Verification Code");

            String content = """
                    <div style="font-family: Arial, sans-serif; max-width:600px; margin:auto;
                                padding:20px; border:1px solid #ddd; border-radius:10px;">
                        
                        <h2 style="color:#2c3e50;">OTP Verification</h2>
                        
                        <p>Hello,</p>
                        
                        <p>Your One Time Password (OTP) for verification is:</p>
                        
                        <div style="text-align:center; margin:20px 0;">
                            <span style="font-size:32px; font-weight:bold; letter-spacing:6px;
                                         color:#ffffff; background:#007bff; padding:10px 20px;
                                         border-radius:8px;">
                                %s
                            </span>
                        </div>
                        
                        <p>This OTP is valid for <b>5 minutes</b>.</p>
                        
                        <p>If you did not request this, please ignore this email.</p>
                        
                        <br/>
                        <p style="color:#777;">CollabXSkill Team</p>
                    </div>
                    """.formatted(otp);

            helper.setText(content, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}