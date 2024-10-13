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
