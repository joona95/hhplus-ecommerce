package kr.hhplus.be.server.interfaces.point;

import kr.hhplus.be.server.domain.point.Amount;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.TransactionType;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.PointFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.point.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.point.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static kr.hhplus.be.server.interfaces.point.PointRequest.*;
import static kr.hhplus.be.server.interfaces.point.PointResponse.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PointControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
    }

    @Nested
    class 유저_잔액_조회 {

        @Test
        void 사용자_포인트를_조회_성공() {

            // given
            User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
            Point point = pointJpaRepository.save(PointFixtures.유저로_잔액_생성(user));

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", String.valueOf(user.getId()));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<UserPointResponse> response = restTemplate.exchange(
                    "/api/v1/points",
                    HttpMethod.GET,
                    entity,
                    UserPointResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().amount()).isEqualTo(point.getAmount());
        }

        @ParameterizedTest
        @ValueSource(longs = {-100L, -10L, -3L, -2L, -1L, 0L})
        void 존재하지_않는_유저식별자로_사용자_포인트_조회_시_500_예외_발생(long userId) {

            //given
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", String.valueOf(userId));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // when
            ResponseEntity<UserPointResponse> response = restTemplate.exchange(
                    "/api/v1/points",
                    HttpMethod.GET,
                    entity,
                    UserPointResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Nested
    class 포인트_충전 {

        @Test
        void 포인트를_충전() {

            // given
            User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
            Point point = pointJpaRepository.save(PointFixtures.유저와_금액으로_잔액_생성(user, 10000));
            int chargeAmount = 10000;
            PointChargeRequest request = new PointChargeRequest(chargeAmount);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", String.valueOf(user.getId()));
            HttpEntity<PointChargeRequest> entity = new HttpEntity<>(request, headers);

            // when
            ResponseEntity<UserPointResponse> response = restTemplate.exchange(
                    "/api/v1/points/charge",
                    HttpMethod.POST,
                    entity,
                    UserPointResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().amount()).isEqualTo(20000);

            List<PointHistory> histories = pointHistoryJpaRepository.findAll();
            assertThat(histories).hasSize(1);
            assertThat(histories.get(0).getAmount()).isEqualTo(Amount.of(chargeAmount));
            assertThat(histories.get(0).getType()).isEqualTo(TransactionType.CHARGE);
        }

        @ParameterizedTest
        @ValueSource(longs = {-100L, -10L, -3L, -2L, -1L, 0L})
        void 존재하지_않는_유저식별자로_포인트_충전_시_500_예외_발생(long userId) {

            // given
            PointChargeRequest request = new PointChargeRequest(10000);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", String.valueOf(userId));
            HttpEntity<PointChargeRequest> entity = new HttpEntity<>(request, headers);

            // when
            ResponseEntity<UserPointResponse> response = restTemplate.exchange(
                    "/api/v1/points/charge",
                    HttpMethod.POST,
                    entity,
                    UserPointResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ParameterizedTest
        @ValueSource(ints = {-100, -10, -3, -2, -1, 0})
        void 양수가_아닌_충전금액으로_포인트_충전(int amount) {

            // given
            User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
            Point point = pointJpaRepository.save(PointFixtures.유저와_금액으로_잔액_생성(user, 10000));
            PointChargeRequest request = new PointChargeRequest(amount);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", String.valueOf(user.getId()));
            HttpEntity<PointChargeRequest> entity = new HttpEntity<>(request, headers);

            // when
            ResponseEntity<UserPointResponse> response = restTemplate.exchange(
                    "/api/v1/points/charge",
                    HttpMethod.POST,
                    entity,
                    UserPointResponse.class
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}