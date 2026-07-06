package stitch.crew.hour.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stitch.crew.hour.product.service.ProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class ProductController {

    private final ProductService productService;

}
