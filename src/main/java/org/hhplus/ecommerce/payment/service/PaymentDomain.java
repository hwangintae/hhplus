package org.hhplus.ecommerce.payment.service;

import lombok.Getter;

@Getter
public class PaymentDomain {
    private Long id;
    private Long orderId;
    private PaymentStatus status;
}
