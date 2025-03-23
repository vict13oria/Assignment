package ing.assessment.controller;

import ing.assessment.db.order.Order;
import ing.assessment.db.product.Product;
import ing.assessment.model.Location;
import ing.assessment.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok().body(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Product>> getProduct(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(productService.getProductsById(id));
    }

    @DeleteMapping("/{productId}/delete-product")
    public ResponseEntity<HttpStatus> deleteProduct(@RequestParam("productId") Integer productId,
                                                    @RequestParam("location") Location location) {
        productService.deleteProductByIdAndLocation(productId, location);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteAllProductS() {
        productService.deleteAllProducts();
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Product> createOrder(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(product));
    }

    @PatchMapping("/{productId}/edit-product-quantity/location/{locationId}")
    public ResponseEntity<Product> editProductQuantity(@PathVariable("productId") Integer productId,
                                                       @PathVariable("locationId") Location location,
                                                       @RequestParam @Valid Integer quantity) {
        return ResponseEntity.ok().body(productService.editProductQuantity(productId, location, quantity));
    }

    @PatchMapping("/{productId}/edit-product-price/location/{locationId}")
    public ResponseEntity<Product> editProductPrice(@PathVariable("productId") Integer productId,
                                                    @PathVariable("locationId") Location location,
                                                    @RequestParam @Valid Double price) {
        return ResponseEntity.ok().body(productService.editProductPrice(productId, location, price));
    }
}