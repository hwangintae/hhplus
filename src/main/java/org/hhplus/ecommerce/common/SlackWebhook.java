package org.hhplus.ecommerce.common;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class SlackWebhook {

    private final Slack slackClient = Slack.getInstance();

    @Value("${slack.url}")
    private String webhookUrl;

    @Value("${slack.path}")
    private String path;

    @Value("${slack.hwang-emergency}")
    private String hwangEmergency;

    public void send(String message) {
        Payload payload = Payload.builder().text(message).build();

        try {
            WebhookResponse response = slackClient.send(webhookUrl + path + hwangEmergency, payload);
        } catch (IOException ignore) {
            log.error(">>> slack에 message를 못보냈어요.. : {}", message);
        }
    }

}
