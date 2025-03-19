package ing.assessment.strategy;

import ing.assessment.db.order.OrderProduct;

public interface CostComputingStrategy {
    public Double computeCostOfOrder(OrderProduct orderProducts);
}
