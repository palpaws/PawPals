package com.he186674.mvc.petshop.service.impl;

import com.he186674.mvc.petshop.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setFrom("pawpals.pet26@gmail.com", "PalPaws");
            helper.setTo(toEmail);
            helper.setSubject("🔐 Mã xác thực OTP - PalPaws");
            helper.setText(
                    "Xin chào,\n\n" +
                    "Mã OTP của bạn là: " + otp + "\n\n" +
                    "Mã có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.\n\n" +
                    "---\n" +
                    "© 2026 PalPaws. All rights reserved."
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email to " + toEmail + ": " + e.getMessage());
        }
    }

    @Override
    public void sendNotificationEmail(String toEmail, String title, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
            helper.setFrom("pawpals.pet26@gmail.com", "PalPaws");
            helper.setTo(toEmail);
            helper.setSubject("🔔 PalPaws - " + title);
            helper.setText(
                    "Xin chào,\n\n" +
                    "Bạn có thông báo mới từ PalPaws:\n\n" +
                    "📌 Tiêu đề: " + title + "\n" +
                    "📝 Nội dung: " + content + "\n\n" +
                    "---\n" +
                    "Đây là email tự động từ PalPaws. Vui lòng không trả lời email này.\n" +
                    "© 2026 PalPaws. All rights reserved."
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
        }
    }
}
