# 회고록

## 문제
> - 주문 시 상품 조회와 결제를 동시에 처리하면서 재고와 잔액의 동시성 문제가 발생.
> - 잔액 충전 및 결제 시 데이터 일관성 문제.
> - 매번 주문 데이터를 조회해 상위 판매 상품을 계산하는 성능 저하 우려.
> - usecase에 코드가 길어지면서 유지보수의 어려움 발생

## 시도
> - 비관적 락을 적용해 트랜잭션 내에서의 동시성 문제 해결 시도.
> - 상품 재고를 확인 후 장바구니에 추가하는 로직 구현.
> - 매일 00시에 스케줄러로 통계 데이터를 저장하도록 설계. // 멘토링 이후 스케줄러 제외함
> - 설계의 문제점을 찾아봄

## 해결
> - 비관적 락으로 결제 시 잔액 차감과 재고 차감의 데이터 일관성 확보.

## 알게 된 것
> - 무조건 락을 걸게 아니라, 정규화를 통해 굳이 락이 안걸려도 되는 부분 분리.
> - 트랜젝션이 걸렸을 때 예외가 발생하면, rollbackOnly option에 의해 try catch 시 에러 발생

## 유지해야 할 것
> - 과제를 포기하지 않고 노력하는 자세

## 개선이 필요한 것
> - 요구 사항에 대해 심플하게 생각할것

## 개선을 위해 시도해야 할 것
> - 어떻게 하면 과제를 신속 정확하게 할 수 있을지 고민