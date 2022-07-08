package com.davacom.CommonService.commands.api;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class  CompleteOrderCommand {

    @TargetAggregateIdentifier
    private String OrderId;
    private String orderStatus;
}
