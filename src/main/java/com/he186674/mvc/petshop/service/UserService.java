package com.he186674.mvc.petshop.service;

import com.he186674.mvc.petshop.entities.User;

public interface UserService {
    User register(String fullName, String email, String password, String confirmPassword);
    User login(String email, String password);
    User findByEmail(String email);
    void setOtpForUser(String email, String otp);
    boolean verifyOtp(String email, String otp);
    String generateResetToken(String email);
    void resetPassword(String email, String password, String token);
}

