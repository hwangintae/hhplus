### 기술 스택
#### 주요 기술
- Java 17
- Spring Boot 3.2.10
#### 빌드 도구
- Gradle(Groovy) 8.7
#### 데이터베이스
- MySQL 8.1.0

### 프로젝트 구조
```
├── ECommerceApplication.java
├── cart
│   ├── controller
│   │   └── CartController.java
│   ├── entity
│   │   └── Cart.java
│   └── service
│       ├── CartItemStatus.java
│       └── CartResponse.java
├── cartItem
│   ├── controller
│   ├── entity
│   │   └── CartItem.java
│   └── service
├── cash
│   ├── controller
│   │   └── CashController.java
│   ├── entity
│   │   ├── Cash.java
│   │   ├── CashHistory.java
│   │   └── CashRepository.java
│   └── service
│       ├── AddCashRequest.java
│       ├── CashDomain.java
│       ├── CashHistoryDomain.java
│       ├── CashResponse.java
│       ├── CashService.java
│       └── TransactionType.java
├── common
│   ├── ApiControllerAdvice.java
│   ├── ApiResponse.java
│   ├── BaseEntity.java
│   └── exception
│       └── EcommerceException.java
├── item
│   ├── controller
│   │   └── ItemController.java
│   ├── entity
│   │   ├── HashTag.java
│   │   ├── Item.java
│   │   ├── Stock.java
│   │   └── TopItem.java
│   └── service
│       ├── HashTagDomain.java
│       ├── ItemDomain.java
│       ├── ItemResponse.java
│       ├── ItemService.java
│       ├── StockDomain.java
│       └── TopItemDomain.java
├── orderItem
│   ├── controller
│   ├── entity
│   │   └── OrderItem.java
│   └── service
│       └── OrderItemDomain.java
├── orders
│   ├── controller
│   │   └── OrdersController.java
│   ├── entity
│   │   └── Orders.java
│   └── service
│       ├── OrderRequest.java
│       ├── OrderRequests.java
│       ├── OrderResponse.java
│       ├── OrderStatus.java
│       ├── OrdersDomain.java
│       └── OrdersService.java
├── payment
│   ├── controller
│   ├── entity
│   │   ├── Payment.java
│   │   └── PaymentHistory.java
│   └── service
│       ├── PaymentDomain.java
│       ├── PaymentHistoryDomain.java
│       ├── PaymentStatus.java
│       └── PaymentType.java
└── user
    ├── controller
    │   └── UserController.java
    ├── entity
    │   ├── User.java
    │   └── UserRepository.java
    └── service
        └── UserService.java

```