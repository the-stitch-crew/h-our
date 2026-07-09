package stitch.crew.hour.reservation.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import stitch.crew.hour.lesson.domain.Lesson;
import stitch.crew.hour.lesson.repository.LessonRepository;
import stitch.crew.hour.policy.domain.LessonPolicy;
import stitch.crew.hour.policy.repository.LessonPolicyRepository;
import stitch.crew.hour.reservation.dto.ReservationRequest;
import stitch.crew.hour.reservation.repository.ReservationRepository;
import stitch.crew.hour.reservation.service.ReservationService;
import stitch.crew.hour.user.constant.Gender;
import stitch.crew.hour.user.constant.Role;
import stitch.crew.hour.user.domain.CurrentUser;
import stitch.crew.hour.user.domain.User;
import stitch.crew.hour.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@Slf4j
class ReservationConcurrencyTest {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReservationService reservationService;
    @Autowired
    LessonRepository lessonRepository;

    @Autowired
    LessonPolicyRepository lessonPolicyRepository;

    @Autowired
    ReservationRepository reservationRepository;


    List<User> users;
    List<Lesson> lessons;
    List<ReservationRequest> reservationRequests = new ArrayList<>();

    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);

    @Nested
    @DisplayName("Describe : ReservationService의 saveReservation() 메서드의 동시성 제어 테스트")
    class Describe_concurrency_test {
        @Nested
        @DisplayName("Context : 100명의 유저가 동시 접속하는 경우")
        class Context_with_100_users {
            @BeforeEach
            void setUp() {
                // 사용자 저장
                users = new ArrayList<>();
                for (int i = 0; i < 100; i++) {
                    User user = new User(
                            "test" + i,
                            "test" + i + "@naver.com",
                            passwordEncoder.encode("password"),
                            LocalDate.of(2003,5,15),
                            Role.USER,
                            Gender.MALE,
                            "",
                            "010-0000-00"+String.format("%02d", i),
                            "대한민국",
                            false,
                            false
                    );
                    users.add(user);
                }
                userRepository.saveAll(users);

                // 수업 저장
                lessons = lessonRepository.saveAll(List.of(
                        new Lesson("레더 카드지갑 클래스", 68000, 3),
                        new Lesson("미니백 클래스", 128000, 4),
                        new Lesson("키링 클래스", 45000, 3)
                ));

                // 정책 조회
                LessonPolicy policy = lessonPolicyRepository.findById(1L)
                        .orElseThrow();

                int[] days = {16, 17, 20, 21};
                Random random = new Random();

                // 예약 요청 생성
                for (int i = 0; i < 100; i++) {
                    Lesson lesson = lessons.get(random.nextInt(lessons.size()));

                    int duration = lesson.getDuration();
                    int startHour = random.nextInt(
                            policy.getStartTime().getHour(),
                            policy.getEndTime().getHour() - duration + 1
                    );
                    reservationRequests.add(
                            new ReservationRequest(
                                    LocalDate.of(
                                            2026,
                                            7,
                                            days[random.nextInt(days.length)]
                                    ),
                                    LocalTime.of(startHour, 0),
                                    LocalTime.of(startHour + duration, 0),
                                    policy.getDepositAmount(),
                                    lesson.getPrice(),
                                    "",
                                    lesson.getId()
                            )
                    );
                }
            }

            @Test
            @DisplayName("It : 락이 적용되지 않은 경우 : 100명의 유저가 동시 예약하는 경우 예약이 무결하지 못한다.")
            void It_create_duplicate_reservations_when_lock_is_not_applied() throws InterruptedException {
                int ITER = 100;  //사용자수
                ExecutorService executor = Executors.newFixedThreadPool(ITER);   //동시에 실행할 스레드 풀
                CountDownLatch startLatch = new CountDownLatch(1);
                CountDownLatch endLatch = new CountDownLatch(ITER);
                for (int i = 0; i < ITER; i++) {
                    int index = i;
                    executor.execute(() -> {
                        try {
                            startLatch.await();
                            reservationService.saveReservation(
                                    new CurrentUser(
                                            users.get(index).getId(),
                                            users.get(index).getEmail(),
                                            Role.USER
                                    ),
                                    reservationRequests.get(index)
                            );

                            successCount.incrementAndGet();
                            ReservationRequest request = reservationRequests.get(index);
                            log.info("예약 성공 ::: date : {}, startTime: {}, endTime : {}, lessonId : {}",request.date(), request.startTime(), request.endTime(), request.lessonId());
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                            log.error("예약 실패 ::: " + e.getMessage());
                        } finally {
                            endLatch.countDown();
                        }
                    });
                }

                startLatch.countDown();
                endLatch.await();
                executor.shutdown();
                long count = reservationRepository.count();

                log.info("성공 요청 : {}, 실패 요청 : {}, 실제 저장 : {}",successCount.get(),failureCount.get(),count);
            }
            @Test
            @DisplayName("It : 100명의 유저가 동일한 예약 시간으로 동시에 예약하는 경우 중복 예약이 발생하지 않는다")
            void It_create_same_reservation_concurrently() throws InterruptedException {

                int ITER = 100;
                ExecutorService executor = Executors.newFixedThreadPool(ITER);
                CountDownLatch startLatch = new CountDownLatch(1);
                CountDownLatch endLatch = new CountDownLatch(ITER);

                // 하나의 예약 요청 생성
                ReservationRequest request = reservationRequests.get(0);

                for (int i = 0; i < ITER; i++) {
                    int index = i;

                    executor.execute(() -> {
                        try {
                            startLatch.await();

                            reservationService.saveReservation(
                                    new CurrentUser(
                                            users.get(index).getId(),
                                            users.get(index).getEmail(),
                                            Role.USER
                                    ),
                                    request
                            );
                            successCount.incrementAndGet();
                            log.info("예약 성공 : {}", index);

                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                            log.error("예약 실패 : {}", e.getMessage());

                        } finally {
                            endLatch.countDown();
                        }
                    });
                }

                // 동시에 시작
                startLatch.countDown();
                endLatch.await();
                executor.shutdown();
                long savedCount = reservationRepository.count();
                log.info("성공 요청 : {}, 실패 요청 : {}, 실제 저장 : {}",successCount.get(),failureCount.get(),savedCount);
            }
        }
    }
}