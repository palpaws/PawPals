package com.he186674.mvc.petshop.controller;

import com.he186674.mvc.petshop.entities.BlogPost;
import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.service.CommunityService;
import com.he186674.mvc.petshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommunityService communityService;

    @GetMapping("/profile")
    public String profile(
            HttpSession session,
            Model model) {

        User currentUser =
                (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);

        try {
            List<BlogPost> myPosts =
                    communityService.getPostsByAuthor(currentUser.getUserId());

            model.addAttribute("myPosts", myPosts);

        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("myPosts", List.of());
        }

        return "profile";
    }
    @GetMapping("/base-profile")
    public String showBaseProfile(HttpSession session) {

        User currentUser =
                (User) session.getAttribute("currentUser");


        if (currentUser == null) {

            return "redirect:/login";

        }


        return "BaseProfile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User currentUser =
                (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/login";
        }

        User updatedUser =
                userService.updateProfile(
                        currentUser.getUserId(),
                        fullName,
                        phone,
                        address
                );

        session.setAttribute(
                "currentUser",
                updatedUser
        );

        redirectAttributes.addFlashAttribute(
                "success",
                "Cập nhật thông tin thành công!"
        );

        return "redirect:/profile";
    }
}