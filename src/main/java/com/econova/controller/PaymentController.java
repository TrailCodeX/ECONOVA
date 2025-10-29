package com.econova.controller;

import com.econova.entity.Order;
import com.econova.entity.Address;
import com.econova.entity.User;
import com.econova.service.OrderService;
import com.econova.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    // Payment method selection
    @GetMapping("/method")
    public String paymentMethodPage(@RequestParam("amount") double amount, 
                                   Model model, 
                                   HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        Address address = (Address) session.getAttribute("selectedAddress");
        if (address == null) {
            return "redirect:/address/form";
        }

        model.addAttribute("amount", amount);
        System.out.println("Payment method page - Amount: " + amount);
        return "payment_method";
    }

    // ✅ UPDATED: COD Order placement - now creates order items
    @PostMapping("/cod/place-order")
    public String placeOrderCOD(@RequestParam("amount") double amount, 
                               Model model, 
                               HttpSession session) {
        try {
            User user = (User) session.getAttribute("currentUser");
            if (user == null) {
                return "redirect:/login";
            }

            Address address = (Address) session.getAttribute("selectedAddress");
            if (address == null) {
                return "redirect:/address/form";
            }

            // Create order with CONFIRMED status
            Order order = new Order();
            order.setUser(user);
            order.setAddress(address);
            order.setAmount(amount);
            order.setCurrency("INR");
            order.setStatus("CONFIRMED");
            order.setPaymentMethod("COD");
            
            // ✅ Use new method that creates order items automatically
            Order savedOrder = orderService.createOrderWithItems(order, user);

            System.out.println("COD Order placed - Order ID: " + savedOrder.getId() + 
                             ", Amount: " + amount + ", Status: CONFIRMED");

            // Clear cart
            cartService.clearCart(user);
            
            // Clear session
            session.removeAttribute("selectedAddress");

            model.addAttribute("orderId", savedOrder.getId());
            return "order_placed";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error placing order: " + e.getMessage());
            return "payment_error";
        }
    }

    // Razorpay payment page
    @GetMapping("/razorpay/page")
    public String razorpayPaymentPage(@RequestParam("amount") double amount, 
                                     Model model, 
                                     HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }

        Address address = (Address) session.getAttribute("selectedAddress");
        if (address == null) {
            return "redirect:/address/form";
        }

        // This creates order with PENDING status and order items
        Order order = orderService.createOrderFromCart(user, address, amount);
        session.setAttribute("currentOrderId", order.getId());

        model.addAttribute("amount", amount);
        model.addAttribute("razorpayKey", orderService.getRazorpayKey());
        
        System.out.println("Razorpay page - Order ID: " + order.getId() + ", Status: PENDING");
        
        return "payment_page";
    }

    // Create Razorpay order
    @PostMapping("/razorpay/createOrder")
    @ResponseBody
    public Map<String, Object> createRazorpayOrder(@RequestParam int amount, 
                                                   HttpSession session) {
        try {
            User user = (User) session.getAttribute("currentUser");
            if (user == null) {
                return Map.of("error", "User not logged in");
            }

            Map<String, Object> order = orderService.createRazorpayOrder(amount);
            
            if (order.containsKey("error")) {
                return order;
            }

            System.out.println("Razorpay order created: " + order.get("id"));
            return order;

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", e.getMessage());
        }
    }

    // ✅ Razorpay payment success - order items already created, just update status
    @PostMapping("/razorpay/success")
    public String razorpayPaymentSuccess(@RequestParam String razorpayPaymentId,
                                        @RequestParam String razorpayOrderId,
                                        @RequestParam(required = false) String razorpaySignature,
                                        Model model,
                                        HttpSession session) {
        try {
            User user = (User) session.getAttribute("currentUser");
            if (user == null) {
                return "redirect:/login";
            }

            System.out.println("Razorpay payment success - Order: " + razorpayOrderId);
            
            // Update payment status to PAID (which sets order status to CONFIRMED)
            // Order items were already created when order was first created
            orderService.updateOrderPayment(razorpayOrderId, razorpayPaymentId, "PAID");
            
            // Clear cart - already cleared in createOrderFromCart, but safe to call again
            cartService.clearCart(user);
            
            session.removeAttribute("selectedAddress");
            session.removeAttribute("currentOrderId");
            
            model.addAttribute("successMessage", "Payment Successful! Order confirmed.");
            return "payment_success";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "payment_error";
        }
    }

    // Razorpay payment failure
    @PostMapping("/razorpay/failed")
    public String razorpayPaymentFailed(@RequestParam String razorpayOrderId,
                                       Model model,
                                       HttpSession session) {
        try {
            User user = (User) session.getAttribute("currentUser");
            if (user == null) {
                return "redirect:/login";
            }

            System.out.println("Razorpay payment failed - Order: " + razorpayOrderId);
            
            orderService.updateOrderPayment(razorpayOrderId, "", "FAILED");
            
            model.addAttribute("errorMessage", "Payment failed. Please try again.");
            return "payment_error";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "payment_error";
        }
    }

    @GetMapping("/status/{orderId}")
    @ResponseBody
    public Map<String, Object> getOrderStatus(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order != null) {
                return Map.of(
                    "id", order.getId(),
                    "status", order.getStatus(),
                    "amount", order.getAmount()
                );
            } else {
                return Map.of("error", "Order not found");
            }
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }
}