package ing.assessment.service;

import ing.assessment.db.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Integer id);
}