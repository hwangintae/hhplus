# 실전! kafka 설치 및 애플리케이션 연동하기
## 사와디캎 (Hello, Kafka 👋)

### 개요 : docker-compose를 이용하여 kafka 설치와 kafka-ui를 통한 message 발행 및 소비를 확인한 문서임.


- docker-compose 를 사용하여 도커에 카프카를 올리고 spring boot와 연결했어요.
- 
    ```
    version: '3.8'
    services:
      zookeeper-1:
        image: confluentinc/cp-zookeeper:7.1.13
        ports:
          - '32181:32181'
        environment:
          ZOOKEEPER_CLIENT_PORT: 32181
          ZOOKEEPER_TICK_TIME: 2000
  
      kafka-1:
        image: confluentinc/cp-kafka:7.1.13
        ports:
          - '9092:9092'
        depends_on:
          - zookeeper-1
        environment:
          KAFKA_BROKER_ID: 1
          KAFKA_ZOOKEEPER_CONNECT: zookeeper-1:32181
          KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT
          KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
          KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka-1:29092,EXTERNAL://localhost:9092
          KAFKA_DEFAULT_REPLICATION_FACTOR: 3
          KAFKA_NUM_PARTITIONS: 3
  
      kafka-2:
        image: confluentinc/cp-kafka:7.1.13
        ports:
          - '9093:9093'
        depends_on:
          - zookeeper-1
        environment:
          KAFKA_BROKER_ID: 2
          KAFKA_ZOOKEEPER_CONNECT: zookeeper-1:32181
          KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT
          KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
          KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka-2:29093,EXTERNAL://localhost:9093
          KAFKA_DEFAULT_REPLICATION_FACTOR: 3
          KAFKA_NUM_PARTITIONS: 3

      kafka-3:
        image: confluentinc/cp-kafka:7.1.13
        ports:
          - '9094:9094'
        depends_on:
          - zookeeper-1
        environment:
          KAFKA_BROKER_ID: 3
          KAFKA_ZOOKEEPER_CONNECT: zookeeper-1:32181
          KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT
          KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
          KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka-3:29094,EXTERNAL://localhost:9094
          KAFKA_DEFAULT_REPLICATION_FACTOR: 3
          KAFKA_NUM_PARTITIONS: 3

      kafka-ui:
        image: provectuslabs/kafka-ui
        container_name: kafka-ui
        ports:
          - "8989:8080"
        restart: always
        environment:
          - KAFKA_CLUSTERS_0_NAME=local
          - KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=kafka-1:29092,kafka-2:29093,kafka-3:29094
          - KAFKA_CLUSTERS_0_ZOOKEEPER=zookeeper-1:22181
    ```
- ![docker-compose](/src/docs/images/kafka/docker-compose.png)
  ```
  spring:
    kafka:
      bootstrap-servers: localhost:9092,localhost:9093,localhost:9094
      producer:
        value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
  ```

- kafka-ui를 올려서 카프카 클러스터가 어떻게 구성되었는지 확인했어요.
- ![kafka-ui1](/src/docs/images/kafka/kafka-ui1.png)
- ![kafka-ui2](/src/docs/images/kafka/kafka-ui2.png)
- ![kafka-ui3](/src/docs/images/kafka/kafka-ui3.png)

- 이벤트를 발행해서 Transacion BEFORE_COMMIT에 outbox table에 init을 추가하고
- AFTER_COMMIT에 카프카 메시지를 발행했어요.
- dir 구조는 다음과 같아요.
    ```
    orders
        ㄴ consumer
        ㄴ OrdersKafkaMessageConsumer
            - void orderTestGroupConsumer(ConsumerRecord<String, String> consumerRecord)
        ㄴ controller
        ㄴ event
            ㄴ OrdersEventListener
                - void orderInitEventSuccessHandler(OrderInitSuccessEvent event)          // BEFORE_COMMIT
                - void orderInitEventKafkaPublish(OrderInitSuccessEvent event)            // AFTER_COMMIT
        ㄴ infra
            ㄴ producer
                ㄴ OrdersKafkaMessagePublisher
                    - void publish(OrderInitSuccessEvent event)
        ㄴ service
    ```
- 다음은 카프카 발행이 정상적으로 작동되는지 확인해 봤어요.
- http client를 사용해서 api 호출을 해봤어요.
    ```http client
    ### 상품 주문 요청
    POST http://localhost:8080/api/orders
    Content-Type: application/json
    
    {
      "userId": 1,
      "orderRequests": [
        {
          "itemId": 1,
          "cnt": 5
        },
        {
          "itemId": 2,
          "cnt": 5
        },
        {
          "itemId": 3,
          "cnt": 100
        }
      ]
    }
    ```
- 카프카 메시지 발행 전, outbox에 init이 추가되고
- ![outboxInit](/src/docs/images/kafka/outboxInit.png)
- 카프카 메시지 발행 후, outbox에 published로 수정됐어요
- ![outboxPublished](/src/docs/images/kafka/outboxPublished.png)
- 카프카 메시지가 잘 발행되는지 kafka-ui messages로 확인해봤어요.
- ![kafka-ui-messages](/src/docs/images/kafka/kafka-ui-messages.png)
- 카프카 메시지가 잘 소비됐는지 kafka-ui consumer로 확인해봤어요.
consumer lag이 0인거보니 잘 소비된거 같아요.
- ![kafka-ui-consumer](/src/docs/images/kafka/kafka-ui-consumer.png)

# 결론
1. docker-compose를 통해 카프카를 관리를 하니 관리의 이점이 크고.
2. kafka-ui를 통해 kafka message의 발행과 소비를 확인할 수 있어 좋았어요.