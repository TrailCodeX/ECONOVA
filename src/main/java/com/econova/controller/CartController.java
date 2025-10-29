package com.econova.controller;

import com.econova.entity.Cart;
import com.econova.entity.User;
import com.econova.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // ✅ View cart page (HTML)
    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Get cart items for the user
            List<Cart> cartItems = cartService.getCartItems(user);
            model.addAttribute("cartItems", cartItems);

            // Calculate totals
            double subtotal = cartService.getTotalPrice(user);
            double shippingFee = 40;
            double total = subtotal + shippingFee;

            model.addAttribute("subtotal", subtotal);
            model.addAttribute("shippingFee", shippingFee);
            model.addAttribute("total", total);

            return "cart";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error loading cart");
            return "error";
        }
    }

    // ✅ Get cart as JSON (for AJAX calls)
    @GetMapping("/view/json")
    @ResponseBody
    public List<Cart> getCartJson(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return List.of();
        }
        return cartService.getCartItems(user);
    }

    // ✅ Add product to cart
    @PostMapping("/add")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session) {
        
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            cartService.addToCart(user, productId, quantity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/customerhome";
    }

    // ✅ Add to cart via AJAX (returns JSON)
    @PostMapping("/add/json")
    @ResponseBody
    public String addToCartJson(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            HttpSession session) {
        
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "{\"error\": \"User not logged in\"}";
        }

        try {
            cartService.addToCart(user, productId, quantity);
            int cartCount = cartService.getCartItemCount(user);
            return "{\"success\": true, \"count\": " + cartCount + "}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    // ✅ Update cart quantities
    @PostMapping("/update")
    public String updateCart(
            @RequestParam(value = "cartIds", required = false) Long[] cartIds,
            @RequestParam(value = "quantities", required = false) int[] quantities,
            HttpSession session) {
        
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            if (cartIds != null && quantities != null) {
                for (int i = 0; i < cartIds.length; i++) {
                    if (i < quantities.length && quantities[i] > 0) {
                        cartService.updateCartQuantity(cartIds[i], quantities[i]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/cart/view";
    }

    // ✅ Remove item from cart
    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam Long cartId,
            HttpSession session) {
        
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            cartService.removeFromCart(cartId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/cart/view";
    }

    // ✅ Remove via AJAX
    @PostMapping("/remove/json")
    @ResponseBody
    public String removeFromCartJson(
            @RequestParam Long cartId,
            HttpSession session) {
        
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "{\"error\": \"User not logged in\"}";
        }

        try {
            cartService.removeFromCart(cartId);
            return "{\"success\": true}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    // ✅ Get cart count (for navbar badge)
    @GetMapping("/count")
    @ResponseBody
    public String getCartCount(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "{\"count\": 0}";
        }
        int count = cartService.getCartItemCount(user);
        return "{\"count\": " + count + "}";
    }

    // ✅ Clear entire cart
    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            cartService.clearCart(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/cart/view";
    }
}