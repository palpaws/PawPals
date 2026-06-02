package com.he186674.mvc.petshop.controller;

import com.he186674.mvc.petshop.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MapController {

    @GetMapping("/map")
    public String mapPage(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");

        // Mặc định tọa độ trung tâm Việt Nam
        model.addAttribute("defaultLat", 21.0285);
        model.addAttribute("defaultLng", 105.8542);

        return "Map";
    }
}