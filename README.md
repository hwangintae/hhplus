# 🏆 lecture_3 주제 : e-commerce

# 사와디Kafka

### **`STEP 17_기본`**

- [x] docker 를 이용해 kafka 를 설치 및 실행하고 애플리케이션과 연결
- [x] 각 프레임워크 (nest.js, spring) 에 적합하게 카프카 consumer, producer 를 연동 및 테스트
- [링크](/src/docs/readme/kafka.md)

### **`STEP 18_심화`**

- [x] 기존에 애플리케이션 이벤트를 카프카 메세지 발행으로 변경
- [x] 카프카의 발행이 실패하는 것을 방지하기 위해 Transactional Outbox Pattern를 적용
- [x] 카프카의 발행이 실패한 케이스에 대한 재처리를 구현 ( Scheduler or BatchProcess )