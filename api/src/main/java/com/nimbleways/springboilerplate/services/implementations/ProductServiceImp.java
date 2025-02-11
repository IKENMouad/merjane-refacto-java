package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.NotificationService;
import com.nimbleways.springboilerplate.services.ProductService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final NotificationService notificationService;

    @Override
    public void notifyDelay(int leadTime, Product product) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }

    private void seasonalProductHandler(Product product) {
        if (isProductOutOfSeason(product))
            markProductOutOfStock(product);
        else if (isSeasonNotStarted(product))
            notifyProductOutOfStock(product);
        else
            notifyDelay(product.getLeadTime(), product);

    }

    private void expiredProductHandler(Product product) {
        if (isProductValid(product))
            decreaseProductQuantity(product);
        else {
            markProductOutOfStock(product);
            notifyProductExpiration(product);
        }
    }

    @Override
    public ProcessOrderResponse processOrder(Long orderId) throws OrderNotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with provider ID " + orderId));

        for (Product product : order.getItems()) {
            switch (product.getType()) {
                case NORMAL:
                    notifyDelay(product.getLeadTime(), product);
                    break;
                case SEASONAL:
                    seasonalProductHandler(product);
                    break;
                case EXPIRABLE:
                    expiredProductHandler(product);
                    break;
                default:
                    break;
            }
        }

        return new ProcessOrderResponse(order.getId());
    }

    private boolean isSeasonNotStarted(Product product) {
        return product.getSeasonStartDate().isAfter(LocalDate.now());
    }

    private boolean isProductValid(Product product) {
        return product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now());
    }

    private boolean isProductOutOfSeason(Product product) {
        return LocalDate.now().plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate());
    }

    private void markProductOutOfStock(Product product) {
        product.setAvailable(0);
        productRepository.save(product);
    }

    private void decreaseProductQuantity(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void notifyProductExpiration(Product product) {
        notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
    }

    private void notifyProductOutOfStock(Product product) {
        notificationService.sendOutOfStockNotification(product.getName());
    }

}