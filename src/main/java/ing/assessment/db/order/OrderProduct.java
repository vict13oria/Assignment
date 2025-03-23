package ing.assessment.db.order;

import ing.assessment.model.Location;
import jakarta.persistence.Embeddable;
import lombok.*;

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