package org.hhplus.ecommerce.payment.service;

import lombok.Getter;

@Getter
public class PaymentHistoryDomain {
    private Long id;
    private Long paymentId;
    private PaymentType type;
}
