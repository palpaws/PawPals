package com.he186674.mvc.petshop.controller;

import com.he186674.mvc.petshop.entities.BlogPost;
import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.service.CommunityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/posts")
public class AdminPostController {

    private final CommunityService communityService;

    public AdminPostController(CommunityService communityService) {
        this.communityService = communityService;
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        return user != null && "ADMIN".equals(user.getRole());
    }

    @GetMapping
    public String listDraftPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                                 HttpSession session,
                                 Model model) {
        if (!isAdmin(session)) return "redirect:/error-404";

        Page<BlogPost> draftPosts = communityService.getDraftPosts(page, 10);

        int currentPageNum = draftPosts.getNumber();
        int totalPagesNum = draftPosts.getTotalPages();

        model.addAttribute("posts", draftPosts.getContent());
        model.addAttribute("currentPage", currentPageNum);
        model.addAttribute("totalPages", totalPagesNum);
        model.addAttribute("totalItems", draftPosts.getTotalElements());

        return "admin-posts";
    }

    @PostMapping("/approve/{id}")
    public String approvePost(@PathVariable Integer id,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/error-404";

        communityService.approvePost(id);
        redirectAttributes.addFlashAttribute("success", "Bài viết đã được duyệt và xuất bản!");

        return "redirect:/admin/posts";
    }

    @PostMapping("/reject/{id}")
    public String rejectPost(@PathVariable Integer id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/error-404";

        communityService.rejectPost(id);
        redirectAttributes.addFlashAttribute("success", "Bài viết đã bị từ chối!");

        return "redirect:/admin/posts";
    }
}
