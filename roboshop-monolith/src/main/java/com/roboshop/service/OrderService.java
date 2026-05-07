package com.roboshop.service;

import com.roboshop.model.*;
import com.roboshop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ShippingService shippingService;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository, CartService cartService,
                        ShippingService shippingService, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.shippingService = shippingService;
        this.paymentService = paymentService;
    }

    @Transactional
    public Order createOrder(User user, String sessionId, Long cityId) {
        return createOrder(user, sessionId, cityId, null);
    }

    @Transactional
    public Order createOrder(User user, String sessionId, Long cityId, String maskedCard) {
        Cart cart = cartService.getOrCreateCart(user, sessionId);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal shippingCost = shippingService.calculateShippingCost(cityId);
        City city = shippingService.getCityById(cityId)
                .orElseThrow(() -> new RuntimeException("City not found"));

        BigDecimal total = cartService.getCartTotal(cart);

        Order order = new Order();
        order.setUser(user);
        order.setTotal(total);
        order.setShippingCost(shippingCost);
        order.setShippingCity(city.getCity());
        order.setStatus("PLACED");

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            order.addItem(orderItem);
        }

        order = orderRepository.save(order);

        // Process mock payment
        paymentService.processPayment(order, maskedCard);

        // Clear the cart after order
        cartService.clearCart(user, sessionId);

        return order;
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
}
