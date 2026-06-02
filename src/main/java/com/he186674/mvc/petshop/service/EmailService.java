package com.he186674.mvc.petshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("khung123450@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Mã OTP PawPals - Đặt lại mật khẩu");
            message.setText("Mã OTP của bạn là: " + otp + "\n\n"
                    + "Mã này sẽ hết hạn trong 5 phút.\n"
                    + "Nếu bạn không yêu cầu này, vui lòng bỏ qua.\n\n"
                    + "Đừng chia sẻ mã này với bất kỳ ai!");

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}
