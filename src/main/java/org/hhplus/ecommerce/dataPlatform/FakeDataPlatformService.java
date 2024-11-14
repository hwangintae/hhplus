package org.hhplus.ecommerce.dataPlatform;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.common.SlackWebhook;
import org.hhplus.ecommerce.common.exception.DataPlatformException;
import org.hhplus.ecommerce.common.exception.EcommerceErrors;
import org.hhplus.ecommerce.orders.event.OrderingSuccessEvent;
import org.hhplus.ecommerce.orders.service.OrderItemRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FakeDataPlatformService implements DataPlatformService {

    private final SlackWebhook slackWebhook;

    @Override
    public void sendData(OrderingSuccessEvent event) {

        List<Long> orderItemIds = event.getOrderItemRequests().stream()
                .map(OrderItemRequest::getOrderItemId)
                .toList();

        try {
            RestTemplate restTemplate = new RestTemplate();
        } catch (Exception e) {
            slackWebhook.send("데이터 플랫폼 전송에 실패했습니다. orderItemIds: " + orderItemIds.toString());
            throw new DataPlatformException(EcommerceErrors.DATA_PLATFORM);
        }
    }
}
