package org.hhplus.ecommerce.docs.payment;

import org.hhplus.ecommerce.cash.controller.CashController;
import org.hhplus.ecommerce.docs.RestDocsSupport;

public class PaymentControllerDocsTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new CashController();
    }
}
