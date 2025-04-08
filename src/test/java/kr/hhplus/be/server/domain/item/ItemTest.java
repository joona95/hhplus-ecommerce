package kr.hhplus.be.server.domain.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ItemTest {

    @Nested
    class 상품_생성 {

        @ParameterizedTest
        @NullAndEmptySource
        void 상품명이_비어있으면_IllegalArgumentException_발생(String itemName) {

            //when, then
            assertThatThrownBy(() -> new Item(1L, itemName, Stock.of(1000), 100, LocalDateTime.now(), LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품명을 입력해주세요.");
        }

        @ParameterizedTest
        @NullSource
        void 재고가_null_이면_IllegalArgumentException_발생(Stock stock) {

            //when, then
            assertThatThrownBy(() -> new Item(1L, "상품명", stock, 100, LocalDateTime.now(), LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고 정보가 필요합니다.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1000, -10, -3, -2, -1})
        void 가격이_음수이면_IllegalArgumentException_발생(int price) {

            //when, then
            assertThatThrownBy(() -> new Item(1L, "상품명", Stock.of(1000), price, LocalDateTime.now(), LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("가격은 음수가 될 수 없습니다.");
        }
    }
}