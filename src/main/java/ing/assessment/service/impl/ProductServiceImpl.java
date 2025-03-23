package ing.assessment.service.impl;

import ing.assessment.db.product.Product;
import ing.assessment.db.product.ProductCK;
import ing.assessment.db.repository.ProductRepository;
import ing.assessment.exception.ItemNotFound;
import ing.assessment.model.Location;
import ing.assessment.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_NOT_FOUND = "Product with ID: '%s' at location: '%s' was not found!";
    private static final String PRODUCTS_NOT_FOUND = "Products with ID: '%s' were not found!";

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
        return Optional.ofNullable(productRepository.findByProductCk_Id(id)).orElseThrow(() -> new ItemNotFound(String.format(PRODUCTS_NOT_FOUND, id)));
    }

    @Override
    public void deleteProductByIdAndLocation(Integer productId, Location location) {
        Product product = productRepository.findByProductCk_IdAndProductCk_Location(productId, location);
        if (product == null)
            throw new ItemNotFound(String.format(PRODUCT_NOT_FOUND, productId, location));
        productRepository.delete(product);
    }

    @Override
    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    @Override
    public Product editProductQuantity(Integer productId, Location location, Integer newQuantity) {
        ProductCK productCK = new ProductCK(productId, location);
        Product product = productRepository.findById(productCK)
                .orElseThrow(() -> new ItemNotFound(String.format(PRODUCT_NOT_FOUND, productId, location)));

        product.setQuantity(newQuantity);
        return productRepository.save(product);
    }

    @Transactional
    @Override
    public Product editProductPrice(Integer productId, Location location, Double newPrice) {
        ProductCK productCK = new ProductCK(productId, location);
        Product product = productRepository.findById(productCK)
                .orElseThrow(() -> new ItemNotFound(String.format(PRODUCT_NOT_FOUND, productId, location)));

        product.setPrice(newPrice);
        return productRepository.save(product);
    }
}