package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.deal.DealQueryRepository;
import com.hcommerce.heecommerce.deal.TimeDealProductNotFoundException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private OrderCommandRepository orderRepository;

    private DealQueryRepository dealQueryRepository;

    @Autowired
    public OrderService(
        OrderCommandRepository orderRepository,
        DealQueryRepository dealQueryRepository
    ) {
        this.orderRepository = orderRepository;
        this.dealQueryRepository = dealQueryRepository;
    }

    public void completeOrderReceipt(UUID orderUuid) {
        UUID resultOrderUuid = orderRepository.updateToCompleteOrderReceipt(orderUuid);

        if(resultOrderUuid == null) {
            throw new OrderNotFoundException(orderUuid);
        }
    }

    public void placeOrder(OrderForm orderForm) {
        validateOrderForm(orderForm);
    }

    private void validateOrderForm(OrderForm orderForm) {
        UUID orderUuid = orderForm.getDealProductUuid();

        validateHasDealProductUuid(orderUuid);

        // TODO : Mybatis 연동이 필요하므로, 다른 PR에서 작업할 예정
        validateHasUserId(orderForm.getUserId());

        validateOrderQuantityInMaxOrderQuantityPerOrder(orderUuid, orderForm.getOrderQuantity());
    }

    private void validateHasDealProductUuid(UUID dealProductUuid) {
        boolean hasDealProductUuid = dealQueryRepository.hasDealProductUuid(dealProductUuid);

        if(!hasDealProductUuid) {
            throw new TimeDealProductNotFoundException(dealProductUuid);
        }
    }

    private void validateHasUserId(int userId) {

    }

    private void validateOrderQuantityInMaxOrderQuantityPerOrder(UUID orderUuid, int orderQuantity) {
        int maxOrderQuantity = dealQueryRepository.getMaxOrderQuantityPerOrderByDealProductUuid(orderUuid);

        if(maxOrderQuantity < orderQuantity) {
            throw new MaxOrderQuantityExceededException(maxOrderQuantity);
        }
    }
}
