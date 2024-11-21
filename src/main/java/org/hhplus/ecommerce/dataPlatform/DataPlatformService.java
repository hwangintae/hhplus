package org.hhplus.ecommerce.dataPlatform;

import org.hhplus.ecommerce.orders.event.OrderPaySuccessEvent;

public interface DataPlatformService {

    void sendData(OrderPaySuccessEvent event);
}
