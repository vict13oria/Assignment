package ing.assessment.db;

import ing.assessment.model.Location;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Double price;
    private Integer quantity;
    private Location location;

    public Product(String pName, Double pPrice, int pQuantity, Location pLocation) {
        this.name = pName;
        this.price = pPrice;
        this.quantity = pQuantity;
        this.location = pLocation;
    }
}