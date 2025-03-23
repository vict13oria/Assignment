package ing.assessment.service;

import ing.assessment.db.product.Product;
import ing.assessment.db.product.ProductCK;
import ing.assessment.db.repository.ProductRepository;
import ing.assessment.exception.ItemNotFound;
import ing.assessment.model.Location;
import ing.assessment.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PriceServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductCK productCK;

    @BeforeEach
    void setUp() {
        productCK = new ProductCK(1, Location.MUNICH);
        product = new Product(productCK, "Laptop", 1200.0, 10);
    }

    @Test
    void getAllProducts_ShouldReturnProductList() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

        List<Product> products = productService.getAllProducts();

        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductsById_ShouldReturnProductList() {
        when(productRepository.findByProductCk_Id(1)).thenReturn(Collections.singletonList(product));

        List<Product> products = productService.getProductsById(1);

        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
        verify(productRepository, times(1)).findByProductCk_Id(1);
    }

    @Test
    void getProductsById_ShouldThrowExceptionWhenNotFound() {
        when(productRepository.findByProductCk_Id(1)).thenReturn(null);

        assertThrows(ItemNotFound.class, () -> productService.getProductsById(1));
    }

    @Test
    void deleteProductByIdAndLocation_ShouldDeleteProduct() {
        when(productRepository.findByProductCk_IdAndProductCk_Location(1, Location.MUNICH)).thenReturn(product);

        productService.deleteProductByIdAndLocation(1, Location.MUNICH);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteProductByIdAndLocation_ShouldThrowExceptionWhenNotFound() {
        when(productRepository.findByProductCk_IdAndProductCk_Location(1, Location.MUNICH)).thenReturn(null);

        assertThrows(ItemNotFound.class, () -> productService.deleteProductByIdAndLocation(1, Location.MUNICH));
    }

    @Test
    void deleteAllProducts_ShouldCallRepositoryDeleteAll() {
        productService.deleteAllProducts();

        verify(productRepository, times(1)).deleteAll();
    }

    @Test
    void createProduct_ShouldSaveProduct() {
        when(productRepository.save(product)).thenReturn(product);

        Product createdProduct = productService.createProduct(product);

        assertNotNull(createdProduct);
        assertEquals("Laptop", createdProduct.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void editProductQuantity_ShouldUpdateQuantity() {
        when(productRepository.findById(productCK)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product updatedProduct = productService.editProductQuantity(1, Location.MUNICH, 15);

        assertNotNull(updatedProduct);
        assertEquals(15, updatedProduct.getQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void editProductQuantity_ShouldThrowExceptionWhenNotFound() {
        when(productRepository.findById(productCK)).thenReturn(Optional.empty());

        assertThrows(ItemNotFound.class, () -> productService.editProductQuantity(1, Location.MUNICH, 15));
    }

    @Test
    void editProductPrice_ShouldUpdatePrice() {
        when(productRepository.findById(productCK)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product updatedProduct = productService.editProductPrice(1, Location.MUNICH, 1300.0);

        assertNotNull(updatedProduct);
        assertEquals(1300.0, updatedProduct.getPrice());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void editProductPrice_ShouldThrowExceptionWhenNotFound() {
        when(productRepository.findById(productCK)).thenReturn(Optional.empty());

        assertThrows(ItemNotFound.class, () -> productService.editProductPrice(1, Location.MUNICH, 1300.0));
    }
}
