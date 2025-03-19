package ing.assessment.db.order;

import ing.assessment.model.Location;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderProduct {

    private Integer productId;
    private Location location;
    private Integer quantity;
}