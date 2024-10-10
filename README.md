# 🏆 lecture_3 주제 : e-commerce

## 1. milestone 작성
> - 이번 발제는 3주동안 진행 됩니다. 따라서 simple 하게 설계, 구현, 고도화 3개로 milestone 을 추가했습니다.
> - github 에 milestone 을 등록할 수 있어서 link 를 첨부하였습니다.
> - https://github.com/hwangintae/hhplus/milestones

### milestone 1. lecture_3 설계
1. milestone 작성하기
2. 요구사항 분석하기
3. sequence diagram 작성하기
4. 개발환경 준비
5. erd 설계하기
6. API 명세 및 Mock API 작성

### milestone 2. lecture_3 구현
1. 잔액 충전 / 조회 API 개발 및 단위 테스트
2. 상품 조회 API 개발 및 단위 테스트
3. 주문 / 결제 API 개발 및 단위 테스트
4. 상위 상품 조회 API 개발 및 단위 테스트
5. 장바구니 기능 개발 및 단위 테스트

### milestone 2. lecture_3 고도화
1. 잔액 충전 / 조회 API 고도화
2. 상품 조회 API 고도화
3. 주문 / 결제 API 고도화
4. 상위 상품 조회 API 고도화
5. 장바구니 기능 고도화


## 2. e-커머스 요구사항 분석 (sequence diagram)
1. 사용자는 잔액을 조회할 수 있고, 충전할 수 있다.
2. 상품 정보를 조회 시, 상품의 정보와 재고를 출력한다.
3. 상품 주문 결제 성공 시, 잔액이 차감 되어야 하며 데이터 분석을 위해 외부 플랫폼에 결과를 전송한다.
### 예상되는 문제
1. 충전과 재고 조회에서 동시성 문제가 발생할 수 있다.
2. 외부 플랫폼에 영향을 받지 않아야 한다. 외부 플랫폼이 정상 작동 하지 않을 경우를 대비 해야한다.

### sequence diagram
#### **`잔액 조회, 충전`**
  - ![cash](https://github.com/user-attachments/assets/404ce1f4-2c5d-4d20-9090-b81aeece05f6)
#### **`상품 조회`**
  - ![item](https://github.com/user-attachments/assets/37ad5f02-c099-4eec-95ab-9f1d1b3cb778)
#### **`상품 주문 및 결제`**
  - ![orderAndPay](https://github.com/user-attachments/assets/8120169a-9e11-4848-9816-2dea1155a54e)

## 3. ERD 설계 자료
1. orders와 item은 N : N 관계라서 orderItem을 만들었습니다. 
2. 결제와 충전은 hitstory를 추가했습니다. 
3. 어떤 상품이 추가될지 모르기 때문에 상품은 추상 클래스로 생성하여 상속할 수 있도록 하였습니다. 
4. 매번 상품과 주문을 조인해서 하는것 보다 “하루에 한번 최근 3일간 가장 많이 팔린 상위 5개 상품 정보“를 따로 저장하는게 효율적이라고 생각합니다. 
5. 상품의 재고를 관리하기 위해 stock을 추가하였습니다. (table lock을 고려)
6. 원래는 카테고리에 상위 카테고리를 위해 parentId를 추가하려고 했으나, depth의 변화에 따라 서비스에 영향을 받을 수 있다고 생각해서 해시태그로 변경했습니다.
7. 장바구니와 사용자는 1:1 이고 장바구니와 상품은 N : N 관계이기 때문에 cartItem을 만들었습니다.
> 멘토링 때 "ERD는 변경될 것이다. 라이프 사이클을 고려해 봐라." 하셨는데 추후 과제에 적극적으로 반영하여 어떻게 하면 이해관계자들과 원활한 의사소통을 할 수 있을지 고민해보겠습니다.
![스크린샷 2024-10-11 오전 2 07 06](https://github.com/user-attachments/assets/01ecc05d-6eb9-4b7a-9c14-0c9057b4b98f)

## 4. API 명세서 및 Mock API
![스크린샷 2024-10-11 오전 3 30 05](https://github.com/user-attachments/assets/18b8e1e0-2ebb-4fab-9f19-a17349497c93)
![스크린샷 2024-10-11 오전 3 30 19](https://github.com/user-attachments/assets/23639a8e-e66c-42e8-aa7d-c0f3739a99a5)
![스크린샷 2024-10-11 오전 3 30 29](https://github.com/user-attachments/assets/653140b6-c3a7-46cf-ac9f-78a35f47db75)
![스크린샷 2024-10-11 오전 3 30 39](https://github.com/user-attachments/assets/4705ed59-d11b-4671-b2da-9e63e156eaa0)
![스크린샷 2024-10-11 오전 3 30 48](https://github.com/user-attachments/assets/46f58c43-bcfd-4fef-8710-d5dbafeb03d8)
![스크린샷 2024-10-11 오전 3 30 58](https://github.com/user-attachments/assets/62b6a01a-1b6f-4856-950e-81c9473c628b)
![스크린샷 2024-10-11 오전 3 31 08](https://github.com/user-attachments/assets/530f5e46-db0d-4c69-a8c4-f4f46842dae5)

> Mock API 호출은 http dir 하위에 있습니다.

## 5. 프로젝트 구조 및 기술 스택
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





## 99. API Specs

### 기본과제

1️⃣ **`주요`** **잔액 충전 / 조회 API**

- [ ] 결제에 사용될 금액을 충전하는 API 를 작성합니다.
- [ ] 사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.
- [ ] 사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.

2️⃣ **`기본` 상품 조회 API**

- [ ] 상품 정보 ( ID, 이름, 가격, 잔여수량 ) 을 조회하는 API 를 작성합니다.
- [ ] 조회시점의 상품별 잔여수량이 정확하면 좋습니다.

3️⃣ **`주요`** **주문 / 결제 API**

- [ ] 사용자 식별자와 (상품 ID, 수량) 목록을 입력받아 주문하고 결제를 수행하는 API 를 작성합니다.
- [ ] 결제는 기 충전된 잔액을 기반으로 수행하며 성공할 시 잔액을 차감해야 합니다.
- [ ] 데이터 분석을 위해 결제 성공 시에 실시간으로 주문 정보를 데이터 플랫폼에 전송해야 합니다. ( 데이터 플랫폼이 어플리케이션 `외부` 라는 가정만 지켜 작업해 주시면 됩니다 )

> 데이터 플랫폼으로의 전송 기능은 Mock API, Fake Module 등 다양한 방법으로 접근해 봅니다.
>

4️⃣ **`기본` 상위 상품 조회 API**

- [ ] 최근 3일간 가장 많이 팔린 상위 5개 상품 정보를 제공하는 API 를 작성합니다.
- [ ] 통계 정보를 다루기 위한 기술적 고민을 충분히 해보도록 합니다.

---

### 심화 과제

5️⃣ **`심화` 장바구니 기능**

- [ ] 사용자는 구매 이전에 관심 있는 상품들을 장바구니에 적재할 수 있습니다.
- [ ] 이 기능을 제공하기 위해 `장바구니에 상품 추가/삭제` API 와 `장바구니 조회` API 가 필요합니다.
- [ ] 위 두 기능을 제공하기 위해 어떤 요구사항의 비즈니스 로직을 설계해야할 지 고민해 봅니다.

💡 **KEY POINT**

- [ ] 동시에 여러 주문이 들어올 경우, 유저의 보유 잔고에 대한 처리가 정확해야 합니다.
- [ ] 각 상품의 재고 관리가 정상적으로 이루어져 잘못된 주문이 발생하지 않도록 해야 합니다.

### **`STEP 05`**
- [x] 시나리오 선정 및 프로젝트 Milestone 제출
- [x] 시나리오 요구사항 별 분석 자료 제출
  > 시퀀스 다이어그램, 플로우 차트 등
- [x] 자료들을 리드미에 작성 후 PR 링크 제출

### **`STEP 06`**

- [x] ERD 설계 자료 제출
- [x] API 명세 및 Mock API 작성
- [x] 자료들을 리드미에 작성 후 PR링크 제출 ( 채택할 기본 패키지 구조, 기술 스택 등 )