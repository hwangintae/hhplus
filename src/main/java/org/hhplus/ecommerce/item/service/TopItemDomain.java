package org.hhplus.ecommerce.item.service;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TopItemDomain {

    private Long id;
    private LocalDate today;
    private Long itemId;
    private int paymentCnt;
}
