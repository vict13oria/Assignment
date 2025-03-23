package ing.assessment.service;

import ing.assessment.db.product.Product;
import ing.assessment.model.Location;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    List<Product> getProductsById(Integer id);
    void deleteProductByIdAndLocation(Integer id, Location location);
    void deleteAllProducts();
    Product createProduct(Product products);
    Product editProductQuantity(Integer id, Location location, Integer quantity);
    Product editProductPrice(Integer id, Location location, Double price);
}