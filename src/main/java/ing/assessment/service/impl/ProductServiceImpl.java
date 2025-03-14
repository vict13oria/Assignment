package ing.assessment.service.impl;

import ing.assessment.db.product.Product;
import ing.assessment.db.repository.ProductRepository;
import ing.assessment.service.ProductService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {


    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsById(Integer id) {
        List<Product> productList = productRepository.findByProductCk_Id(id);
        return productList;
    }
}