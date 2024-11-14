package org.hhplus.ecommerce.dataPlatform;

import org.hhplus.ecommerce.orders.event.OrderingSuccessEvent;

public interface DataPlatformService {

    void sendData(OrderingSuccessEvent event);
}
