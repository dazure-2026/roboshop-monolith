package com.roboshop.service;

import com.roboshop.model.*;
import com.roboshop.repository.CartItemRepository;
import com.roboshop.repository.CartRepository;
import com.roboshop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Cart getOrCreateCart(User user, String sessionId) {
        if (user != null) {
            return cartRepository.findByUser(user).orElseGet(() -> {
                Cart cart = new Cart();
                cart.setUser(user);
                return cartRepository.save(cart);
            });
        }
        return cartRepository.findBySessionId(sessionId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setSessionId(sessionId);
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public Cart addToCart(User user, String sessionId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(user, sessionId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            cart.addItem(item);
        }
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateQuantity(User user, String sessionId, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(user, sessionId);
        cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .ifPresent(item -> {
                    if (quantity <= 0) {
                        cart.removeItem(item);
                    } else {
                        item.setQuantity(quantity);
                    }
                });
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeFromCart(User user, String sessionId, Long itemId) {
        Cart cart = getOrCreateCart(user, sessionId);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(User user, String sessionId) {
        Cart cart = getOrCreateCart(user, sessionId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public BigDecimal getCartTotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCartItemCount(Cart cart) {
        return cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    }

    @Transactional
    public void mergeSessionCartToUser(String sessionId, User user) {
        Optional<Cart> sessionCart = cartRepository.findBySessionId(sessionId);
        if (sessionCart.isPresent()) {
            Cart userCart = getOrCreateCart(user, null);
            for (CartItem item : sessionCart.get().getItems()) {
                addToCart(user, null, item.getProduct().getId(), item.getQuantity());
            }
            cartRepository.delete(sessionCart.get());
        }
    }
}
