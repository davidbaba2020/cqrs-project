package com.davacom.PaymentService.command.api.aggregate;

import com.davacom.CommonService.commands.api.CancelPaymentCommand;
import com.davacom.CommonService.commands.api.ValidatePaymentCommand;
import com.davacom.CommonService.events.PaymentProcessedEvents;
import com.davacom.CommonService.events.PaymentCancelledEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@Slf4j
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    private String paymentStatus;

    public PaymentAggregate() {
    }

    @CommandHandler
    public PaymentAggregate(ValidatePaymentCommand validatePaymentCommand) {
        //Validate the payment details
        //Publish the payment process event
        log.info("Executing validatePaymentCommand for Order Id {}",
                validatePaymentCommand.getOrderId(),
                validatePaymentCommand.getPaymentId());

        PaymentProcessedEvents paymentProcessedEvents = new PaymentProcessedEvents(
                validatePaymentCommand.getPaymentId(),
                validatePaymentCommand.getOrderId()
        );

        AggregateLifecycle.apply(paymentProcessedEvents);

        log.info("PaymentProcessedEvents Applied ");
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvents event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
    }


    @CommandHandler
    public void handle(CancelPaymentCommand cancelPaymentCommand) {
        log.info("Executing validatePaymentCommand for Order Id {}",
                cancelPaymentCommand.getOrderId());
        PaymentCancelledEvent paymentCancelledEvent = new PaymentCancelledEvent();
        BeanUtils.copyProperties(cancelPaymentCommand, paymentCancelledEvent);
        AggregateLifecycle.apply(paymentCancelledEvent);
    }

    @EventSourcingHandler
    public void on(PaymentCancelledEvent event) {
        this.paymentStatus = event.getPaymentStatus();
    }

}
