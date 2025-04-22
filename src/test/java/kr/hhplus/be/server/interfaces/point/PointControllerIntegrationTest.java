package kr.hhplus.be.server.interfaces.point;

import kr.hhplus.be.server.domain.point.Amount;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.TransactionType;
import kr.hhplus.be.server.fixtures.PointFixtures;
import kr.hhplus.be.server.infrastructure.point.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.point.PointJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PointControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;

    @BeforeEach
    void setUp() {
        pointHistoryJpaRepository.deleteAll();
        pointJpaRepository.deleteAll();
    }

    @Test
    void 사용자_포인트를_조회() {

        // given
        Point point = pointJpaRepository.save(PointFixtures.금액으로_잔액_생성(10000));

        // when
        ResponseEntity<PointResponse.UserPointResponse> response = restTemplate.getForEntity(
                "/api/v1/points?userId=" + point.getUserId(),
                PointResponse.UserPointResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().amount()).isEqualTo(10000);
    }

    @ParameterizedTest
    @ValueSource(longs = {-100L, -10L, -3L, -2L, -1L, 0L})
    void 양수가_아닌_유저식별자로_사용자_포인트_조회(long userId) {

        // when
        ResponseEntity<PointResponse.UserPointResponse> response = restTemplate.getForEntity(
                "/api/v1/points?userId=" + userId,
                PointResponse.UserPointResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 포인트를_충전() {

        // given
        Point point = pointJpaRepository.save(PointFixtures.금액으로_잔액_생성(10000));
        Long userId = point.getUserId();
        int chargeAmount = 10000;

        PointRequest.PointChargeRequest request = new PointRequest.PointChargeRequest(userId, chargeAmount);

        // when
        ResponseEntity<PointResponse.UserPointResponse> response = restTemplate.postForEntity(
                "/api/v1/points/charge",
                request,
                PointResponse.UserPointResponse.class
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
    void 양수가_아닌_유저식별자로_포인트_충전(long userId) {

        // given
        PointRequest.PointChargeRequest request = new PointRequest.PointChargeRequest(userId, 10000);

        // when
        ResponseEntity<PointResponse.UserPointResponse> response = restTemplate.postForEntity(
                "/api/v1/points/charge",
                request,
                PointResponse.UserPointResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, -10, -3, -2, -1, 0})
    void 양수가_아닌_충전금액으로_포인트_충전(int amount) {

        // given
        PointRequest.PointChargeRequest request = new PointRequest.PointChargeRequest(1L, amount);

        // when
        ResponseEntity<PointResponse.UserPointResponse> response = restTemplate.postForEntity(
                "/api/v1/points/charge",
                request,
                PointResponse.UserPointResponse.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}