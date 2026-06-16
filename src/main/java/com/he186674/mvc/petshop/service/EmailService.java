package com.he186674.mvc.petshop.service;

public interface EmailService {
    void sendNotificationEmail(String toEmail, String title, String content);
    void sendOtpEmail(String toEmail, String otp);
}
