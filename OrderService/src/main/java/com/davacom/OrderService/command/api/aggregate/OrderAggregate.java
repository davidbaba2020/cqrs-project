package com.davacom.OrderService.command.api.aggregate;


import com.davacom.CommonService.commands.api.CancelOrderCommand;
import com.davacom.CommonService.commands.api.CompleteOrderCommand;
import com.davacom.CommonService.events.OrderCancelledEvent;
import com.davacom.CommonService.events.OrderCompletedEvent;
import com.davacom.OrderService.command.api.command.CreateOrderCommand;
import com.davacom.OrderService.command.api.event.OrderCreatedEvent;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private String addressId;
    private Integer quantity;
    private String orderStatus;


    public OrderAggregate() {
    }


    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {

        //Validate The command
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();

        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);

        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.userId = event.getUserId();
        this.addressId = event.getAddressId();
        this.productId = event.getProductId();
        this.quantity = event.getQuantity();
        this.orderStatus = event.getOrderStatus();
    }

    @CommandHandler
    public void handle(CompleteOrderCommand completeOrderCommand) {
        //Validate command
        //Publish command
        OrderCompletedEvent orderCompletedEvent = OrderCompletedEvent.builder()
                .OrderId(completeOrderCommand.getOrderId())
                .orderStatus(completeOrderCommand.getOrderStatus())
                .build();
        AggregateLifecycle.apply(orderCompletedEvent);

    }

    @EventSourcingHandler
    public void on(OrderCompletedEvent event) {
        this.orderStatus = event.getOrderStatus();
    }


    @CommandHandler
    public void handle(CancelOrderCommand cancelOrderCommand) {
        OrderCancelledEvent orderCompletedEvent = new OrderCancelledEvent();

        BeanUtils.copyProperties(cancelOrderCommand, orderCompletedEvent);

        AggregateLifecycle.apply(orderCompletedEvent);
    }


    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        this.orderStatus = event.getOrderStatus();
    }
}
