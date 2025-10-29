package com.econova.controller;

import com.econova.entity.Address;
import com.econova.entity.User;
import com.econova.service.AddressService;
import com.econova.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private CartService cartService;

    // Show address form
    @GetMapping("/form")
    public String showAddressForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("address", new Address());
        List<Address> addresses = addressService.getAddressesByUser(user);
        model.addAttribute("addresses", addresses);
        return "address";
    }

    // Save new address
    @PostMapping("/save")
    public String saveAddress(@ModelAttribute Address address, HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        address.setUser(user);
        addressService.saveAddress(address);
        
        model.addAttribute("successMessage", "Address saved successfully!");
        model.addAttribute("address", new Address());
        model.addAttribute("addresses", addressService.getAddressesByUser(user));
        return "address";
    }

    // Select address and proceed to payment
    @PostMapping("/select")
    public String selectAddress(@RequestParam(value = "addressId", required = true) Long addressId,
                               HttpSession session, 
                               Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Validate address belongs to user
            Address address = addressService.getAddressById(addressId);
            if (address == null) {
                model.addAttribute("errorMessage", "Address not found");
                model.addAttribute("address", new Address());
                model.addAttribute("addresses", addressService.getAddressesByUser(user));
                return "address";
            }

            if (!address.getUser().getId().equals(user.getId())) {
                model.addAttribute("errorMessage", "Invalid address selected");
                model.addAttribute("address", new Address());
                model.addAttribute("addresses", addressService.getAddressesByUser(user));
                return "address";
            }

            // Store selected address in session
            session.setAttribute("selectedAddress", address);

            // Calculate cart total
            double subtotal = cartService.getTotalPrice(user);
            double total = subtotal + 40; // 40 is shipping fee

            System.out.println("Address selected - ID: " + addressId + 
                             ", Subtotal: " + subtotal + ", Total: " + total);

            // Redirect to payment method page
            return "redirect:/payment/method?amount=" + total + "&subtotal=" + subtotal;

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error selecting address: " + e.getMessage());
            model.addAttribute("address", new Address());
            model.addAttribute("addresses", addressService.getAddressesByUser(user));
            return "address";
        }
    }
}