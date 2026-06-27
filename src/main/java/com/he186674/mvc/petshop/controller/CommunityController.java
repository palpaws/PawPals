package com.he186674.mvc.petshop.controller;

import com.he186674.mvc.petshop.dto.*;
import com.he186674.mvc.petshop.entities.BlogCategory;
import com.he186674.mvc.petshop.entities.BlogPost;
import com.he186674.mvc.petshop.entities.User;
import com.he186674.mvc.petshop.repository.BlogCategoryRepository;
import com.he186674.mvc.petshop.repository.BlogCommentRepository;
import com.he186674.mvc.petshop.repository.UserRepository;
import com.he186674.mvc.petshop.service.CommunityService;
import jakarta.servlet.http.HttpSession;
 




 
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;
    private final BlogCategoryRepository blogCategoryRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final UserRepository userRepository;

 
    private final String uploadDir = "src/main/resources/static/uploads/community";

    public CommunityController(CommunityService communityService,
                               BlogCategoryRepository blogCategoryRepository,
                               BlogCommentRepository blogCommentRepository,
                               UserRepository userRepository) {
        this.communityService = communityService;
        this.blogCategoryRepository = blogCategoryRepository;
        this.blogCommentRepository = blogCommentRepository;
        this.userRepository = userRepository;
    }

    private String saveUploadedFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/community/" + filename;
    }

    @GetMapping
    public String feed(@RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size,
                       HttpSession session,
                       Model model) {
        Page<FeedDto> feedPage = communityService.getFeed(keyword, page, size);

        int currentPageNum = feedPage.getNumber();
        int totalPagesNum  = feedPage.getTotalPages();
        int rangeStart = Math.max(0, currentPageNum - 2);
        int rangeEnd   = Math.min(totalPagesNum - 1, currentPageNum + 2);

        model.addAttribute("posts", feedPage.getContent());
        model.addAttribute("currentPage", currentPageNum);
        model.addAttribute("totalPages", totalPagesNum);
        model.addAttribute("totalItems", feedPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageRangeStart", rangeStart);
        model.addAttribute("pageRangeEnd", rangeEnd);
        model.addAttribute("showFirstPage",      rangeStart > 0);
        model.addAttribute("showFirstEllipsis",  rangeStart > 1);
        model.addAttribute("showLastEllipsis",   rangeEnd < totalPagesNum - 2);
        model.addAttribute("showLastPage",       rangeEnd < totalPagesNum - 1);
        model.addAttribute("isFirstPage",        currentPageNum == 0);
        model.addAttribute("isLastPage",         currentPageNum >= totalPagesNum - 1);
        model.addAttribute("prevPage",           Math.max(0, currentPageNum - 1));
        model.addAttribute("nextPage",           Math.min(totalPagesNum - 1, currentPageNum + 1));
        model.addAttribute("lastPageIndex",      Math.max(0, totalPagesNum - 1));

        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);

        return "community-feed";
    }

    @GetMapping("/post/{id}")
    public String postDetail(@PathVariable("id") Integer postId,
                             HttpSession session,
                             Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        Integer userId = (currentUser != null) ? currentUser.getUserId() : null;

        PostDetailDto post = communityService.getPostDetail(postId, userId);
        List<CommentTreeDto> comments = communityService.getCommentTree(postId);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("currentUser", currentUser);

        return "community-detail";
    }

    @GetMapping("/create")
    public String showCreateForm(HttpSession session,
                                 Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<BlogCategory> categories = blogCategoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("createPostDto", new CreatePostDto());

        return "community-create";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute CreatePostDto createPostDto,
                             @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            // Handle file upload
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String thumbnailUrl = saveUploadedFile(thumbnailFile);
                createPostDto.setThumbnailUrl(thumbnailUrl);
            }

            communityService.createPost(createPostDto, currentUser.getUserId());
            redirectAttributes.addFlashAttribute("success", "Post created successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
            return "redirect:/community/create";
        }

        return "redirect:/community";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer postId,
                               HttpSession session,
                               Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        PostDetailDto post = communityService.getPostDetail(postId, currentUser.getUserId());
        if (!post.getAuthorId().equals(currentUser.getUserId()) && !"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/community";
        }

        // Không cho sửa bài đã bị từ chối (HIDDEN)
        BlogPost rawPost = communityService.getPostsByAuthor(post.getAuthorId())
                .stream().filter(p -> p.getPostId().equals(postId)).findFirst().orElse(null);
        if (rawPost != null && "HIDDEN".equals(rawPost.getStatus()) && !"ADMIN".equals(currentUser.getRole())) {
            return "redirect:/profile";
        }

        List<BlogCategory> categories = blogCategoryRepository.findAll();

        CreatePostDto createPostDto = new CreatePostDto();
        createPostDto.setTitle(post.getTitle());
        createPostDto.setCategoryId(post.getCategoryId());
        createPostDto.setThumbnailUrl(post.getThumbnailUrl());
        createPostDto.setContent(post.getContent());

        model.addAttribute("postId", postId);
        model.addAttribute("categories", categories);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("createPostDto", createPostDto);

        return "community-edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable("id") Integer postId,
                             @ModelAttribute CreatePostDto createPostDto,
                             @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            // Handle file upload (only if a new file is provided)
            if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                String thumbnailUrl = saveUploadedFile(thumbnailFile);
                createPostDto.setThumbnailUrl(thumbnailUrl);
            }

            communityService.updatePost(postId, createPostDto);
            redirectAttributes.addFlashAttribute("success", "Post updated successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
            return "redirect:/community/edit/" + postId;
        }

        return "redirect:/community/post/" + postId;
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Integer postId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            communityService.deletePost(postId, currentUser.getUserId());
            redirectAttributes.addFlashAttribute("success", "Post deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/community";
    }

    @GetMapping("/post/{id}/comments")
    @ResponseBody
    public Map<String, Object> getComments(@PathVariable("id") Integer postId) {
        Map<String, Object> result = new HashMap<>();
        List<CommentTreeDto> comments = communityService.getCommentTree(postId);
        result.put("success", true);
        result.put("comments", comments);
        return result;
    }

    @PostMapping("/post/{id}/like")
    @ResponseBody
    public Map<String, Object> toggleLike(@PathVariable("id") Integer postId,
                                          HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "Please login first");
            return result;
        }

        boolean liked = communityService.toggleLike(postId, currentUser.getUserId());
        Long likeCount = communityService.getPostDetail(postId, currentUser.getUserId()).getLikeCount();

        result.put("success", true);
        result.put("liked", liked);
        result.put("likeCount", likeCount);

        return result;
    }

    @PostMapping("/post/{id}/comment")
    @ResponseBody
    public Map<String, Object> addComment(@PathVariable("id") Integer postId,
                                          @RequestParam("content") String content,
                                          HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "Please login first");
            return result;
        }

        communityService.addComment(postId, currentUser.getUserId(), content);
        List<CommentTreeDto> comments = communityService.getCommentTree(postId);

        result.put("success", true);
        result.put("comments", comments);

        return result;
    }

    @PostMapping("/comment/{commentId}/reply")
    @ResponseBody
    public Map<String, Object> addReply(@PathVariable("commentId") Integer commentId,
                                        @RequestParam("content") String content,
                                        HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "Please login first");
            return result;
        }

        communityService.addReply(commentId, currentUser.getUserId(), content);

        // Get the postId from the comment to return updated comment tree
        var commentOpt = blogCommentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Integer postId = commentOpt.get().getPost().getPostId();
            List<CommentTreeDto> comments = communityService.getCommentTree(postId);
            result.put("comments", comments);
        }

        result.put("success", true);

        return result;
    }
}
