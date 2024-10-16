package org.hhplus.ecommerce.dataPlatform;

import org.hhplus.ecommerce.orders.service.OrderItemDomain;

import java.util.List;

public interface DataPlatformService {

    void sendData(List<OrderItemDomain> orderItemDomains);
}
