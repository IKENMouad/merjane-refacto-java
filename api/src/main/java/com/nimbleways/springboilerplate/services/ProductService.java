package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;

public interface ProductService {
    ProcessOrderResponse processOrder(Long orderId) throws OrderNotFoundException;

    void notifyDelay(int leadTime, Product product);
}