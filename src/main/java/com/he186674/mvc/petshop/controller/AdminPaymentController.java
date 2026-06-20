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
import java.util.List;

@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    public AdminPaymentController(PaymentRepository paymentRepository,
                                  UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    // CHECK ADMIN
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user != null && "ADMIN".equals(user.getRole());
    }

    // LIST
    @GetMapping
    public String list(HttpSession session, Model model) {

        if (!isAdmin(session)) return "redirect:/error-404";

        List<Payment> payments = paymentRepository.findAll();
        model.addAttribute("payments", payments);

        return "admin-payments";
    }

    // APPROVE
    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Integer id, HttpSession session) {

        if (!isAdmin(session)) return "redirect:/error-404";

        Payment p = paymentRepository.findById(id).orElse(null);
        if (p != null) {

            p.setStatus("SUCCESS");
            p.setPaidAt(LocalDateTime.now());
            paymentRepository.save(p);

            User u = userRepository.findById(p.getUserId()).orElse(null);
            if (u != null) {
                u.setPremiumExpiredAt(LocalDateTime.now().plusDays(30));
                userRepository.save(u);
            }
        }

        return "redirect:/admin/payments";
    }

    // REJECT
    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Integer id, HttpSession session) {

        if (!isAdmin(session)) return "redirect:/error-404";

        Payment p = paymentRepository.findById(id).orElse(null);
        if (p != null) {
            p.setStatus("FAILED");
            paymentRepository.save(p);
        }

        return "redirect:/admin/payments";
    }
}