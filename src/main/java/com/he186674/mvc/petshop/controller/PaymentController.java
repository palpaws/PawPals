package com.he186674.mvc.petshop.controller;


import com.he186674.mvc.petshop.entities.Payment;
import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.repository.PaymentRepository;
import com.he186674.mvc.petshop.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public PaymentController(
            PaymentRepository paymentRepository,
            UserRepository userRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    // ======================================================
    // 1. PREMIUM PAGE
    // ======================================================
    @GetMapping("/premium")
    public String premiumPage(HttpSession session, Model model) {

        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        return "premium";
    }

    // ======================================================
    // 2. CREATE PAYMENT + SHOW QR PAGE
    // ======================================================
    @PostMapping("/premium/pay")
    public String createPayment(HttpSession session, Model model) {

        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            return "redirect:/login";
        }

        Payment payment = new Payment();
        payment.setUserId(user.getUserId());
        payment.setAmount(49000);
        payment.setStatus("PENDING");
        payment.setOrderId(String.valueOf(System.currentTimeMillis()));
        payment.setRequestId("REQ_" + System.currentTimeMillis());
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        model.addAttribute("payment", payment);

        return "redirect:/premium/success";
    }

    // ======================================================
    // 3. USER CONFIRM (CLICK "I PAID")
    // ======================================================
    @PostMapping("/premium/confirm/{paymentId}")
    public String confirmPayment(@PathVariable Integer paymentId,
                                 HttpSession session) {

        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            return "redirect:/login";
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow();

        // update payment
        payment.setStatus("SUCCESS");
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // upgrade user
        user.setPremiumExpiredAt(
                LocalDateTime.now().plusDays(30)
        );

        userRepository.save(user);

        // update session
        session.setAttribute("currentUser", user);

        return "redirect:/premium/success";
    }

    // ======================================================
    // 4. SUCCESS PAGE
    // ======================================================
    @GetMapping("/premium/success")
    public String success() {
        return "premium-success";
    }

    // ======================================================
    // 5. CHECK PREMIUM STATUS (OPTION UTILITY)
    // ======================================================
    public boolean isPremium(User user) {

        return user.getPremiumExpiredAt() != null
                && user.getPremiumExpiredAt().isAfter(LocalDateTime.now());
    }
}
