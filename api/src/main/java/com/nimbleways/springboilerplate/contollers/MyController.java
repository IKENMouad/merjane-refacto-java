package com.nimbleways.springboilerplate.contollers;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.services.ProductService;

import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class MyController {

    @Autowired
    private ProductService productService;

    @PostMapping("{orderId}/processOrder")
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public ProcessOrderResponse processOrder(@PathVariable Long orderId) {
           return productService.processOrder(orderId);         
    }
}