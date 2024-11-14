# 🏆 데이터 분석 및 query 성능 개선

- ### index란?
    - 어플리케이션을 운영하면 데이터가 생성된다. (물론 트래픽이 없으면 안생긴다..)
  데이터가 생성되면 어느 순간 데이터가 많아서 조회에 많은 시간이 걸린다.
  조회 성능을 높히기 위한 방법 중에 index가 있다.
    - index는 B-tree 로 데이터를 저장된다.(innoDB는 B+tree로 저장되고 BTREE로 표기함)
  1 부터 11까지의 값을 저장한다고 했을 때 B-tree 는 다음과 같이 저장된다.
    - ![btree](/src/docs/images/query/btree.png)
    - 만약 5을 찾는다고 하면 맨 위에서 부터 탐색을 시작 하는데(root node 부터) 탐색하는 node와 값을 비교하여 다음 위치로 이동한다.
    - 왜 이렇게 할까? 이유는 순차 탐색과 비교를 해보면 효율을 알 수 있다.
    - 만약 데이터가 1 부터 11까지 순차적으로 저장되어 있고 5를 찾는다면 어떻게 할까?
  1부터 탐색을 시작하면 총 5번의 탐색을 한다.
  11부터 탐색을 시작하면 7번의 탐색을 한다.
  그런데 B-tree에서는 2번만에 탐색이 가능하다!
    - 자세한 B-tree의 생성과 탐색 과정은 [링크](https://www.cs.usfca.edu/~galles/visualization/BTree.html)를 참고바란다.

- ### 데이터 분석 및 성능 개선
  - 그렇다면 index를 생성한다고 무조건 좋을까? 정답은 어떻게 index를 생성하는지에 따라 성능이 다르다.
  더 정확히는 데이터의 특성에 맞게, 하려는 목적에 맞게 생성을 해야한다.
  - 다음은 테스트 데이터에 대한 주석과 query를 나열했다.
  
  ```
  -- 총 개수 23,800,000
  select count(id)
    from order_item
  ;
  
  
  -- 저장된 데이터 일 수 1000일
  select count(distinct ordered_at)
    from order_item
  ;
  
  
  -- 데이터 범위 2022-02-17 ~ 2024-11-12
  select min(ordered_at)
       , max(ordered_at)
    from order_item
  ;
  
  
  -- 보통 일일 4400 개 씩 주문 됐는데
  -- 2024-11-04 ~ 2024-11-10애 갑자기 주문량이 많아짐 20,330,800
  -- 2024-11-04 : 3,504,400
  -- 2024-11-10 : 2,804,400
  select ordered_at
       , count(ordered_at)
    from order_item
   group by ordered_at
   order by ordered_at desc
  ;
  
  
  -- item_id는 1 ~ 50이 있다.
  select distinct item_id
    from order_item
   order by item_id
  ;
  ```

  - 다음은 pk index만 있고 비교적 적은 데이터를 read 할 때 query와 실행 계획이다.

  ```
  -- 데이터가 비교적 적을 때
  -- index : id(PK)
  -- rows : 23,105,749
  -- filtered : 11.11
  -- execution: 5 s 491 ms, fetching: 19 ms
  explain
  select item_id
       , sum(item_cnt)
    from order_item
   where ordered_at between DATE_FORMAT('2024-10-23', '%Y-%m-%d') and DATE_FORMAT('2024-10-30', '%Y-%m-%d')
   group by item_id
   order by sum(item_cnt)
   limit 5
  ;
  ```
  ![lowDataNoIndexExplain](/src/docs/images/query/lowDataNoIndexExplain.png)
  ![lowDataNoIndexPlan](/src/docs/images/query/lowDataNoIndexPlan.png)

- 아래와 같은 이유로 인덱스를 생성하였다.
  ```
  -- ordered_at의 데이터 범위가 크고, item_id 가 정렬 되어 있을 때 sum()을 하기 편하기 때문에
  -- ordered_at과 item_id로 인덱스를 생성한다. (카디널리티가 ordered_at이 크기 때문에 ordered_at, item_id 순으로 한다,)
  create index idx_ordered_at_item_id on order_item(ordered_at, item_id);
  drop index idx_ordered_at_item_id on order_item;
  ```
  ![indexResult](/src/docs/images/query/indexResult.png)
  
- 인덱스 결과 탐색하는 rows 도 줄었고, filtered도 높아졌다.
  ```
  -- index : id(PK), idx_ordered_at_item_id
  -- rows : 70566
  -- filtered : 100
  -- execution: 92 ms, fetching: 10 ms
  explain
  select item_id
       , sum(item_cnt)
    from order_item
   where ordered_at between DATE_FORMAT('2024-10-23', '%Y-%m-%d') and DATE_FORMAT('2024-10-30', '%Y-%m-%d')
   group by item_id
   order by sum(item_cnt)
   limit 5
  ;
  ```
  ![lowDataIndexExplain](/src/docs/images/query/lowDataIndexExplain.png)
  ![lowDataIndexPlan](/src/docs/images/query/lowDataIndexPlan.png)

- 그러나, 일 평균 300만 건 7일 총합 2천만건 정도 되었을 때는, 인덱스를 설정해도 똑똑한 옵티마이져가 full scan을 한다. (내 맘 같지 않다.)
- 옵티마이져가 index scan을 할 때 보다. full scan을 하는게 더 좋다고 판단하면 index를 무시한다.
  ```
  -- 데이터가 비교적 많을 때
  -- index : id(PK)
  -- rows : 23,688,860
  -- filtered : 11.11
  -- execution: 7 s 869 ms, fetching: 26 ms
  explain
  select item_id
       , sum(item_cnt)
    from order_item
   where ordered_at between DATE_FORMAT('2024-11-04', '%Y-%m-%d') and DATE_FORMAT('2024-11-10', '%Y-%m-%d')
   group by item_id
   limit 5
  ;
  ```
  ```
  -- rows : 23,688,860
  -- filtered : 50
  -- index : idx_ordered_at_item_id
  -- execution: 8 s 190 ms, fetching: 13 ms
  -- 더 느려짐.. 절망..
  explain
  select item_id
       , sum(item_cnt)
    from order_item
   where ordered_at between DATE_FORMAT('2024-11-04', '%Y-%m-%d') and DATE_FORMAT('2024-11-10', '%Y-%m-%d')
   group by item_id
   limit 5
  ;
  ```
  
  - 그렇다면 어떻게 해야 조회 성능을 올릴 수 있을까?
  - between 과 같은 범위 탐색 보다. in 절을 이용하면 eq 조회를 하기 때문에 성능의 이점이 있다고 한다. [링크](https://jojoldu.tistory.com/565)
  ```
  -- rows : 23,105,749
  -- filtered : 100
  -- index : idx_ordered_at_item_id
  -- execution: 5 s 417 ms, fetching: 21 ms
  explain
  select item_id
       , sum(item_cnt)
    from order_item
   where ordered_at in (DATE_FORMAT('2024-11-04', '%Y-%m-%d'),
                        DATE_FORMAT('2024-11-05', '%Y-%m-%d'),
                        DATE_FORMAT('2024-11-06', '%Y-%m-%d'),
                        DATE_FORMAT('2024-11-07', '%Y-%m-%d'),
                        DATE_FORMAT('2024-11-08', '%Y-%m-%d'),
                        DATE_FORMAT('2024-11-09', '%Y-%m-%d'),
                        DATE_FORMAT('2024-11-10', '%Y-%m-%d')
                       )
   group by item_id
   limit 5
  ;
  ```
  - 하지만 내가 원하는 수준의 성능이 나오진 않는다.
  - 결론, 인덱스를 이용하여 조회 성능을 높이는 것은 분명한 한계가 존재한다.
  그렇기 때문에 통계 테이블을 생성하여 집계된 데이터를 조회만 하는 방식으로 문제를 풀던가
  아니면 캐시를 이용하여 조회성능을 올려야한다.

### 시도 했지만 못함
  - 내가 만약 주문 데이터 플랫폼을 개발한다면 어떻게 했을까? 를 생각해 봤어요.
  - 나중에 데이터가 엄청 많아진다면 RDB를 이용하는 것 보다. NoSql을 이용하는게 편리할 것으로 판단했어요. 
  - mongoDB에 데이터를 넣고 샤딩을 해보려고 했어요. (23,800,000row를 다 넣었는데...)
  - ![insertData](/src/docs/images/query/insertData.png)
  - 주문 날짜인 orderedAt을 키로 해서 샤딩을 해야겠다. 샤딩을 하려면 어떻게 구성을 해야하지 고민하고 있었는데
  - 데이터가 다 날라갔어요..😢