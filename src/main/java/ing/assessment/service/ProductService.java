package ing.assessment.service;

import ing.assessment.db.product.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    List<Product> getProductsById(Integer id);
}