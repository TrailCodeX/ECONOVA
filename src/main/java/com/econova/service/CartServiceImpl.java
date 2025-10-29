package com.econova.service;

import com.econova.entity.Cart;
import com.econova.entity.Product;
import com.econova.entity.User;
import com.econova.repository.CartRepository;
import com.econova.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void addToCart(User user, Long productId, int quantity) {
        // Fetch product from DB
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if the item already exists in the cart
        Cart cartItem = cartRepository.findByUserAndProduct(user, product)
                .orElse(new Cart());

        cartItem.setUser(user);
        cartItem.setProduct(product);

        // Update quantity if item already exists
        if (cartItem.getId() != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem.setQuantity(quantity);
        }

        // Save cart item
        cartRepository.save(cartItem);
    }

    @Override
    public List<Cart> getCartItems(User user) {
        return cartRepository.findByUser(user);
    }

    @Override
    public void updateCartQuantity(Long cartId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cart.setQuantity(quantity);
        cartRepository.save(cart);
    }

    @Override
    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Override
    public void clearCart(User user) {
        List<Cart> items = cartRepository.findByUser(user);
        cartRepository.deleteAll(items);
    }

    @Override
    public int getCartItemCount(User user) {
        return cartRepository.findByUser(user).size();
    }

    @Override
    public double getTotalPrice(User user) {
        List<Cart> items = cartRepository.findByUser(user);
        return items.stream()
                .mapToDouble(cart -> cart.getProduct().getPrice() * cart.getQuantity())
                .sum();
    }
}
