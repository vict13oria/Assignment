package ing.assessment.db.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    private Integer id;

    private Date timestamp;
    @ElementCollection // todo: change; also, address the arrayList below to hold the qty as well
    private List<OrderProduct> orderProducts;
    private Double orderCost;
}