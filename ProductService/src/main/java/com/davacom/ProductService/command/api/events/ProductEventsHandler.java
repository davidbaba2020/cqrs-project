package com.davacom.ProductService.command.api.events;


import com.davacom.ProductService.command.api.data.Product;
import com.davacom.ProductService.command.api.data.ProductRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ProductEventsHandler {

    private final ProductRepository productRepository;



    public ProductEventsHandler(ProductRepository productRepository)  {
        this.productRepository = productRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
        Product product = new Product();
        BeanUtils.copyProperties(event,product);

        productRepository.save(product);
    }
}
