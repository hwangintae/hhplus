# 🏆 트랜젝션의 범위와 한계
 
- ### 황인태요 e-commerce 분석
  - 매주 고민이던 OrdersFacade의 크기.. 주문 요청이 들어오면
  한개의 Transaction(이하 Tx)에서 재고 차감도 하고, 결제도 하고, 주문 상품 등록도 했다.
  - Tx의 크기가 너무 크다! Tx의 크기를 줄여보자! 라고 생각해서 OrdersFacade에 있던 @Transaction을 제거했다.
  - Tx의 크기를 줄인 이유는 한 Tx에서 여러 서비스가 실행될 때 **신속 정확** 하게 만 끝나면 정말 행복한 상황이다.
  - 그런데 만약 단 하나의 서비스가 실행 속도가 느려지면 어떻게 될까?
  - 그리고 마지막 서비스에서 예외가 발생하면 어떻게 될까?
  - ![longTransaction](/src/docs/images/transaction/longTransaction.png)
  - 특정 서비스 때문에 전체 서비스가 느려지고 rollback이 발생하는 문제가 생긴다. 그렇기 때문에 Tx를 작게 설계해야 한다.


- ### 황인태요 e-commerce의 문제점
  - 각각 서비스에서 독립적으로 Tx가 작동한다. 그렇기 때문에 만약 재고 차감을 했는데 결제에서 실패를 했을 경우, 
  재고를 다시 원상복구 시키는 보상 서비스를 생성했다.
  - 그러나 @Transaction만 제거 되었지 여전히 OrdersFacade에서 하는 일이 많다.
  - ![beforeOrdersFacade](/src/docs/images/transaction/beforeOrdersFacade.png)
  - 시퀀스 다이어그램을 보면 주문의 Activation이 엄청 길다!

 
- ### 황인태요 e-commerce의 해결 방안
  - 어떻게 하면 주문 Activation의 길이를 줄일 수 있을까?
  - 주문의 Activation이 긴 이유는 주문이 다른 서비스를 과도하게 의존하기 때문이다. (주문을 하려면 재고도 알아야하고, 잔액도 알아야하고..)
  ```
  // 주문이 재고도 호출하고, 잔액도 호출한다.
  주문() {
    재고();
    잔액();
  }
  ```
- 이벤트를 사용하여 관심사를 분리해보자!
- 단, 지금의 usecase에서 주문 서비스 완료 시 데이터 플랫폼 전송만 이벤트 발송 처리를 한다.
- 이벤트 처리를 위한 과정은 아래와 같다.
  ```
  @Transactional
  public List<OrderItemDomain> createOrders(Long userId, List<OrderRequest> orderRequests) {

      // ... 생략 ...

      List<OrderItem> saves = orderItemRepository.save(orderItems);

      List<OrderItemDomain> results = saves.stream()
              .map(OrderItem::toDomain)
              .toList();

      List<OrderItemRequest> orderItemRequests = results.stream()
              .map(OrderItemDomain::toRequest)
              .toList();

      // 이벤트 발행
      ordersEventPublisher.success(new OrderingSuccessEvent(orderItemRequests));

      return results;
  }
  ```
  ```
  @Component
  @RequiredArgsConstructor
  public class OrdersEventPublisher {
  private final ApplicationEventPublisher eventPublisher;
  
      public void success(OrderingSuccessEvent event) {
  
          eventPublisher.publishEvent(event);
      }
  }
  ```
  ```
  @Slf4j
  @Component
  @RequiredArgsConstructor
  public class OrdersEventListener {
  private final DataPlatformService dataPlatformService;
  
      @Async
      @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
      public void orderingSuccessHandler(OrderingSuccessEvent event) {
          dataPlatformService.sendData(event);
      }
  }
  ```
  ```
  @Getter
  public class OrderingSuccessEvent {
  
      private final List<OrderItemRequest> orderItemRequests;
  
      public OrderingSuccessEvent(List<OrderItemRequest> orderItemRequests) {
          this.orderItemRequests = orderItemRequests;
      }
  }
  ```
  - 이렇게 하게되면 주문 service와 데이터 플랫폼 요청이 분리가 된다.
  - 그리고 데이터 플랫폼이 실패했을 시, 다른 서비스에 영향을 미치지 않게 되지만 추후 데이터 플랫폼의 데이터 보정을 위해 슬랙에 로그 메시지를
  전달하도록 하였다. (a.k.a 황멀젼시)
  ![slackEmergency](/src/docs/images/transaction/slackEmergency.png)