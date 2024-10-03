# TODO
### **`Default`**
- [x] 아키텍처 준수를 위한 애플리케이션 패키지 설계
- [x] 특강 도메인 테이블 설계 및 목록/신청 등 기본 기능 구현
- [x] 각 기능에 대한 **단위 테스트** 작성

### **`STEP 3`**
- [x] 설계한 테이블에 대한 **ERD** 및 이유를 설명하는 **README** 작성
- [x] 선착순 30명 이후의 신청자의 경우 실패하도록 개선
- [x] 동시에 동일한 특강에 대해 40명이 신청했을 때, 30명만 성공하는 것을 검증하는 **통합 테스트** 작성

### **`STEP 4`**
- [x] 같은 사용자가 동일한 특강에 대해 신청 성공하지 못하도록 개선
- [x] 동일한 유저 정보로 같은 특강을 5번 신청했을 때, 1번만 성공하는 것을 검증하는 **통합 테스트** 작성

### **`API Specs`**

1️⃣ **(핵심) 특강 신청 API**
- [x] 특정 userId 로 선착순으로 제공되는 특강을 신청하는 API 를 작성합니다.
- [x] 동일한 신청자는 동일한 강의에 대해서 한 번의 수강 신청만 성공할 수 있습니다.
- [x] 특강은 선착순 30명만 신청 가능합니다.
- [x] 이미 신청자가 30명이 초과되면 이후 신청자는 요청을 실패합니다.

2️⃣ **특강 선택 API**
- [x] 현재 신청 가능한 특강 목록을 조회하는 API 를 작성합니다.
- [x] 특강의 정원은 30명으로 고정이며, 사용자는 각 특강에 신청하기전 목록을 조회해 볼 수 있어야 합니다.

3️⃣ **특강 신청 완료 목록 조회 API**
- [x] 특정 userId 로 신청 완료된 특강 목록을 조회하는 API 를 작성합니다.
- [x] 각 항목은 특강 ID 및 이름, 강연자 정보를 담고 있어야 합니다.

# ERD
> erd 사진은 따로 첨부하였음

## DDL
### **공통사항**
- row 가 추가 또는 삭제가 되었을 때 언제 되었는지 확인하기 위해 BaseEntity 를 extends 하였다.
- 모든 pk 는 auto_increment 로 관리된다.
 ### **lecture**
  - 수강 신청 기간에 수강 신청을 하고, 강의 시간에 강의를 해야한다는 생각으로 4개의 시간을 나눴다.
  - 원래 목적은 '코치가 강의를 개설한다.'가 있었지만 멘토링 이후 코치 테이블은 삭제하였다.
  - 강의 하면 학점, 장소, 온라인 강의 등 생각나서 추가해봤다.
  - 최대 수용 인원을 lecture 에 추가하여 최대 인원을 통제한다.
  ```
  create table db_lecture_srv.lecture
  (
      id bigint auto_increment primary key,
      coach_id bigint,
      title varchar(255),
      register_start_date_time datetime(6),
      register_end_date_time datetime(6),
      lecture_start_date_time datetime(6),
      lecture_end_date_time datetime(6),
      credit int,
      room_name varchar(255),
      online bit,
      max_capacity int,
      created_at datetime(6),
      updated_at datetime(6)
  );
  ```
### **current_lecture_capacity**
- 이 테이블의 목적은 수강신청을 했을 때, current_capacity 가 증가되는데 이때 비관적 락을 사용하여 테이블 접근을 막는다.
- 테이블 접근이 불가능할 때, lecture 에 대한 수정 요청이 발생할 수 있기 때문에 lecture 에 lock 을 걸지않기 위해 1:1 관계의 테이블을 추가하였다.
- lecture_id가 중복되면 안되기 때문에 lecture_id에 unique 설정을 했다.
  ```
  create table db_lecture_srv.current_lecture_capacity
  (
      id bigint auto_increment primary key,
      lecture_id bigint not null,
      current_capacity int,
      created_at datetime(6),
      updated_at datetime(6),
      unique (lecture_id)
  );
  ```
### **lecture_enrollment**
- 어떤 학생이 어떤 강의를 수강했는지 등록하기 위해 만들었다.
- 수강 취소를 생각해서 deleteAt를 추가했는데 따로 service 에서 구현하진 않았다.
  ```
  create table db_lecture_srv.lecture_enrollment
  (
      id bigint auto_increment primary key,
      lecture_id bigint,
      student_id bigint,
      delete_at bit,
      created_at datetime(6),
      updated_at datetime(6)
  );
  ```



# 궁금한거 (멘토링으로 해결)
- 코치가 강의를 개설할 때, coachService에 lectureRepository를 주입해서 저장하는게 맞을까? 아니면 facade 에서 각각 서비스를 주입받아서 사용해야 하나
  ```
  // code_1
  @Service
  @RequiredArgsConstructor
  public class CoachService {
    private final CoachRepository coachRepository;
    private final LectureRepository lectureRepository;
    
    @Transactional
    public void createLecture(LectureCreate create) {
      Coach coach = coachREpository.findById(crate.getCoachId());
      Lecture lecture = coach.crateLecture(crate);
      
      lectureRepository.save(lecture);
    }
  }
  ```
  > lecture 의 수정은 담당 coach 만 할 수 있다는 정책이 있을 경우,
  > lectureService 에서 coach 를 조회해서 수정해야할지 coachService 에서 coach 를 조회해서 수정해야할지 고민이 된다.

  ```
  // code_2
  @Component
  @RequiredArgsConstructor
  public class LectureFacade {
    private final CoachService coachService;
    private final LectureService lectuerService;
  
    @Transactional
    public void createLecture(lectureCreate create) {
      CoachDomain coachDomain = coachService.getCoach(create.getCoachId());
      lectureService.save(coachDomain.cretaeLectrue(create));
    }
  }
  ```
  > 코치를 생성할 때는 coachController 에서 coachService 를 주입해서 구현하면 될거 같은데,
  > lectureFacade 가 생겨서 lectureController 는 facade 를 주입 한다면. controller 간 차이가 발생한다.
  > 이때, 계층간 역할을 확실하게 하기 위해 coachFacade 를 만들어야 하는가?


