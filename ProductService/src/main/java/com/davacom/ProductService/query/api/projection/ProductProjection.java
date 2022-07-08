package com.davacom.ProductService.query.api.projection;

import com.davacom.ProductService.command.api.data.Product;
import com.davacom.ProductService.command.api.data.ProductRepository;
import com.davacom.ProductService.core.api.model.ProductRestModel;
import com.davacom.ProductService.query.api.queries.GetProductQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductProjection {

    private ProductRepository productRepository;

    public ProductProjection(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @QueryHandler
    public List<ProductRestModel> handle(GetProductQuery getProductQuery) {

        List<Product> products = productRepository.findAll();

        List<ProductRestModel> productRestModels =
                products.stream()
                        .map(product-> ProductRestModel.builder()
                                .name(product.getName())
                                .price(product.getPrice())
                                .quantity(product.getQuantity())
                                .build()).collect(Collectors.toList());
        return productRestModels;
    }
}
