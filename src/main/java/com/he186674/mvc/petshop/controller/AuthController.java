package com.he186674.mvc.petshop.controller;

import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.service.UserService;
import com.he186674.mvc.petshop.service.impl.CalendarNotificationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Controller
public class AuthController {

    private final UserService userService;
    private final CalendarNotificationService calendarNotificationService;

    @Autowired
    public AuthController(UserService userService, CalendarNotificationService calendarNotificationService) {
        this.userService = userService;
        this.calendarNotificationService = calendarNotificationService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "Login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.login(email, password);

            session.setAttribute("currentUser", user);

            // tạo notification
            calendarNotificationService.generateReminderNotifications(user.getUserId());

            // =========================
            // PHÂN QUYỀN Ở ĐÂY
            // =========================
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                return "redirect:/admin/payments";
            }

            return "redirect:/";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("emailVal", email);
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "Register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {
        try {
            userService.register(fullName, email, password, confirmPassword);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Hãy đăng nhập.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("fullNameVal", fullName);
            redirectAttributes.addFlashAttribute("emailVal", email);
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "ForgotPassword";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("email") String email,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(email);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Email không tồn tại");
                return "redirect:/forgot-password";
            }

            String otp = String.format("%04d", new Random().nextInt(10000));
            userService.setOtpForUser(email, otp);

            session.setAttribute("resetEmail", email);
            redirectAttributes.addFlashAttribute("success", "Mã OTP đã được gửi đến email của bạn");
            return "redirect:/verify-otp";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forgot-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage() {
        return "VerifyOTP";
    }

    @PostMapping("/verify-otp")
    public String handleVerifyOtp(
            @RequestParam("otp") String otp,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            String email = (String) session.getAttribute("resetEmail");
            if (email == null) {
                redirectAttributes.addFlashAttribute("error", "Phiên hết hạn. Vui lòng thử lại.");
                return "redirect:/forgot-password";
            }

            userService.verifyOtp(email, otp);
            String resetToken = userService.generateResetToken(email);
            session.setAttribute("resetToken", resetToken);
            return "redirect:/reset-password";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/verify-otp";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/verify-otp";
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            String email = (String) session.getAttribute("resetEmail");
            if (email != null) {
                String otp = String.format("%04d", new Random().nextInt(10000));
                userService.setOtpForUser(email, otp);
                redirectAttributes.addFlashAttribute("success", "Mã OTP mới đã được gửi");
            }
            return "redirect:/verify-otp";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/verify-otp";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(HttpSession session, org.springframework.ui.Model model) {
        String token = (String) session.getAttribute("resetToken");
        if (token == null) {
            return "redirect:/forgot-password";
        }
        model.addAttribute("resetToken", token);
        return "ResetPassword";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("token") String token,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            String email = (String) session.getAttribute("resetEmail");
            if (email == null) {
                redirectAttributes.addFlashAttribute("error", "Phiên hết hạn. Vui lòng thử lại.");
                return "redirect:/forgot-password";
            }

            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp.");
                return "redirect:/reset-password";
            }

            userService.resetPassword(email, password, token);
            session.removeAttribute("resetEmail");
            session.removeAttribute("resetToken");
            return "redirect:/reset-success";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reset-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/reset-password";
        }
    }

    @GetMapping("/reset-success")
    public String resetSuccessPage() {
        return "ResetSuccess";
    }

    @PostMapping("/login/google")
    @ResponseBody
    public ResponseEntity<?> handleGoogleLogin(
            @RequestParam("credential") String credential,
            HttpSession session) {

        try {
            // Xác thực Google ID token bằng cách gọi Google API
            URL url = new URL("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + credential);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return ResponseEntity.badRequest().body(Map.of("error", "Token Google không hợp lệ."));
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseBody.append(line);
            }
            in.close();

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> tokenInfo = mapper.readValue(responseBody.toString(), Map.class);

            String email = (String) tokenInfo.get("email");
            String name = (String) tokenInfo.get("name");

            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Không thể lấy email từ tài khoản Google."));
            }

            // Kiểm tra email đã có tài khoản chưa
            User user = userService.findByEmail(email);

            if (user == null) {
                // Email chưa đăng ký → tự động tạo tài khoản mới
                try {
                    user = userService.registerWithGoogle(name != null ? name : email.split("@")[0], email);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Không thể tạo tài khoản từ Google: " + e.getMessage()));
                }
            }

            // Kiểm tra tài khoản active
            if (user.getIsActive() == null || !user.getIsActive()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tài khoản đã bị khóa."));
            }

            session.setAttribute("currentUser", user);
            calendarNotificationService.generateReminderNotifications(user.getUserId());

            // Phân quyền - trả về redirect URL để frontend xử lý
            String redirectUrl = "/";
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                redirectUrl = "/admin/payments";
            }

            return ResponseEntity.ok(Map.of("success", true, "redirect", redirectUrl));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi đăng nhập Google: " + e.getMessage()));
        }
    }

    @GetMapping("/error-404")
    public String error404Page() {
        return "Error404";
    }
}

