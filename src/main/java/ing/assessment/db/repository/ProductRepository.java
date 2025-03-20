package ing.assessment.db.repository;

import ing.assessment.db.product.Product;
import ing.assessment.db.product.ProductCK;
import ing.assessment.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, ProductCK> {
    List<Product> findByProductCk_Id(Integer id);
    Product findByProductIdAndOrderId(Integer productId, Location orderId);
}