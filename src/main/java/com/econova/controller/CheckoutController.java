package com.econova.controller;

import com.econova.entity.User;
import com.econova.service.AddressService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CheckoutController {

    @Autowired
    private AddressService addressService;

    // Show checkout page with saved addresses
    @GetMapping("/checkout")
    public String showCheckoutPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        // Fetch addresses of logged-in user
        model.addAttribute("addresses", addressService.getAddressesByUser(user));
        return "address"; // Thymeleaf template
    }
}
