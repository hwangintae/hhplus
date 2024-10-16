package org.hhplus.ecommerce.dataPlatform;

import org.hhplus.ecommerce.orders.service.OrderItemDomain;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FakeDataPlatformService implements DataPlatformService {

    @Override
    public void sendData(List<OrderItemDomain> orderItemDomains) {
    }
}
