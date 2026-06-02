package com.he186674.mvc.petshop.service.impl;

import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.repository.UserRepository;
import com.he186674.mvc.petshop.service.EmailService;
import com.he186674.mvc.petshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public User register(String fullName,
                         String email,
                         String password,
                         String confirmPassword) {

        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ và tên không được để trống.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp.");
        }

        if (userRepository.existsByEmail(email.trim())) {
            throw new IllegalArgumentException("Email đã được sử dụng.");
        }

        User user = new User();

        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPasswordHash(passwordEncoder.encode(password));

        user.setRole("USER");
        user.setIsActive(true);

        user.setCurrentStreak(0);
        user.setLongestStreak(0);

        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }

        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException("Email hoặc mật khẩu không chính xác."));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không chính xác.");
        }

        if (user.getIsActive() == null || !user.getIsActive()) {
            throw new IllegalArgumentException("Tài khoản đã bị khóa.");
        }

        user.setLastLoginDate(LocalDate.now());

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByEmail(email.trim()).orElse(null);
    }

    @Override
    public void setOtpForUser(String email, String otp) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }

        User user = findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email không tồn tại.");
        }

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        emailService.sendOtpEmail(email, otp);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }

        if (otp == null || otp.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã OTP không được để trống.");
        }

        User user = findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email không tồn tại.");
        }

        if (user.getOtp() == null || user.getOtpExpiry() == null) {
            throw new IllegalArgumentException("Mã OTP chưa được gửi.");
        }

        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new IllegalArgumentException("Mã OTP đã hết hạn. Vui lòng gửi lại.");
        }

        if (!user.getOtp().equals(otp.trim())) {
            throw new IllegalArgumentException("Mã OTP không chính xác.");
        }

        return true;
    }

    @Override
    public String generateResetToken(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }

        User user = findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email không tồn tại.");
        }

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);
        return resetToken;
    }

    @Override
    public void resetPassword(String email, String password, String token) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất một chữ cái in hoa.");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất một chữ cái in thường.");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất một số.");
        }

        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token không hợp lệ.");
        }

        User user = findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Email không tồn tại.");
        }

        if (user.getResetToken() == null || !user.getResetToken().equals(token)) {
            throw new IllegalArgumentException("Token không hợp lệ.");
        }

        if (user.getResetTokenExpiry() == null || LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new IllegalArgumentException("Token đã hết hạn. Vui lòng yêu cầu đặt lại mật khẩu mới.");
        }

        user.setPasswordHash(passwordEncoder.encode(password));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }
}

