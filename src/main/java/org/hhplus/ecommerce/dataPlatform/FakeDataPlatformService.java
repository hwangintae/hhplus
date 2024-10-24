package org.hhplus.ecommerce.dataPlatform;

import org.hhplus.ecommerce.orders.service.OrderItemDomain;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FakeDataPlatformService implements DataPlatformService {

    @Async
    @Override
    public void sendData(List<OrderItemDomain> orderItemDomains) {

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getForObject("", String.class);
    }
}
