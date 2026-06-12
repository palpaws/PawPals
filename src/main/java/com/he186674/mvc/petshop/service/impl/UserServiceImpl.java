package com.he186674.mvc.petshop.service.impl;

import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.repository.UserRepository;
import com.he186674.mvc.petshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
    public User updateProfile(Integer userId,
                              String fullName,
                              String phone,
                              String address) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Không tìm thấy người dùng."));

        user.setFullName(fullName.trim());

        user.setPhone(
                phone == null ? null : phone.trim()
        );

        user.setAddress(
                address == null ? null : address.trim()
        );

        return userRepository.save(user);
    }
}