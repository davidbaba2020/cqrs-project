package com.davacom.OrderService.command.api.saga;


import com.davacom.CommonService.commands.api.*;
import com.davacom.CommonService.events.*;
import com.davacom.CommonService.models.User;
import com.davacom.CommonService.queries.api.GetUserPaymentDetailsQuery;
import com.davacom.OrderService.command.api.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
@Slf4j
public class OrderProcessingSaga {

    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient QueryGateway queryGateway;


    public OrderProcessingSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent event) {
        log.info("OrderCreatedEvent in Saga for Order Id : {}", event.getOrderId());

        GetUserPaymentDetailsQuery getUserPaymentDetailsQuery = new GetUserPaymentDetailsQuery(event.getUserId());

        User user = null;
        try {
            user = queryGateway.query(
                    getUserPaymentDetailsQuery,
                    ResponseTypes.instanceOf(User.class)
            ).join();

        } catch (Exception e) {
            log.error(e.getMessage());
            //Start the compensating transaction
            cancelOrderCommand(event.getOrderId());
        }


        ValidatePaymentCommand validatePaymentCommand = ValidatePaymentCommand.builder()
                .orderId(event.getOrderId())
                .cardDetails(user.getCardDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();
        commandGateway.sendAndWait(validatePaymentCommand);
    }

    private void cancelOrderCommand(String orderId) {
        CancelOrderCommand CancelOrderCommand = new CancelOrderCommand(orderId);
        commandGateway.send(CancelOrderCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    private void handle(PaymentProcessedEvents event) {
        log.info("OrderCreatedEvent in saga for Order id : {}",
                event.getOrderId());
        try {

            if(true)
                throw new Exception();
            ShipmentOrderCommand shipmentOrderCommand = ShipmentOrderCommand.builder()
                    .shipmentId(UUID.randomUUID().toString())
                    .orderId(event.getOrderId())
                    .build();
            commandGateway.send(shipmentOrderCommand);
        } catch (Exception e) {
            log.error(e.getMessage());
            //Start compensating transaction.
            cancelPaymentCommand(event);
        }
    }

    private void cancelPaymentCommand(PaymentProcessedEvents event) {
        CancelPaymentCommand cancelPaymentCommand= new CancelPaymentCommand(
                event.getPaymentId(), event.getOrderId()
        );
        commandGateway.send(cancelPaymentCommand);
    }


    @SagaEventHandler(associationProperty = "orderId")
    private void handle(OrderShippedEvent event) {
        log.info("OrderShipmentEvent saga for Order id : {}",
                event.getOrderId());
        try {
            CompleteOrderCommand completeOrderCommand = CompleteOrderCommand.builder()
                    .OrderId(event.getOrderId())
                    .orderStatus("APPROVED")
                    .build();
            commandGateway.send(completeOrderCommand);
        } catch (Exception e) {
            log.error(e.getMessage());
            //Start compensating transaction.
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    private void handle(OrderCompletedEvent event) {
        log.info("OrderCompletedEvent saga for Order id : {}",
                event.getOrderId());

        //Will implement email sender before end saga but for now, let me follow tutorial and end saga here.
//        try {
//            CompleteOrderCommand completeOrderCommand = CompleteOrderCommand.builder()
//                    .orderStatus(event.getOrderStatus())
//                    .build();
//            commandGateway.send(completeOrderCommand);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            //Start compensating transaction.
//        }
    }


    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderCancelledEvent event) {
        log.info("OrderCancelledEvent saga for Order id : {}",
                event.getOrderId());
    }


    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentCancelledEvent event) {
        log.info("PaymentCancelledEvent saga for Order id : {}",
                event.getOrderId());
        cancelOrderCommand(event.getOrderId());
    }
}
