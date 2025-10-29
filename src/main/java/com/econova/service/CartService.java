package com.econova.service;

import com.econova.entity.Cart;
import com.econova.entity.User;
import java.util.List;

public interface CartService {

    // Add a product to the cart
    void addToCart(User user, Long productId, int quantity);

    // Get all cart items for a user
    List<Cart> getCartItems(User user);

    // Update quantity of a cart item
    void updateCartQuantity(Long cartId, int quantity);

    // Remove a specific item from the cart
    void removeFromCart(Long cartId);

    // Clear all cart items for a user
    void clearCart(User user);

    // Get total number of items in the cart
    int getCartItemCount(User user);

    // ✅ Calculate total price of all items in the user's cart
    double getTotalPrice(User user);
}
