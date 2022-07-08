package com.davacom.CommonService.events;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCompletedEvent {
    private String OrderId;
    private String orderStatus;
}
