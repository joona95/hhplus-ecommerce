DELIMITER $$

DROP PROCEDURE IF EXISTS insertDummyCoupons$$

CREATE PROCEDURE insertDummyCoupons()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE j INT DEFAULT 1;
    WHILE i <= 1
        DO
            INSERT INTO coupon (id, coupon_name, count, discount_type, discount_value, valid_from, valid_to,
                                created_at, updated_at)
            VALUES (i,
                    CONCAT('쿠폰', i),
                    1000000,
                    'FIXED',
                    50000,
                    now() - INTERVAL 1 DAY,
                    now() + INTERVAL 7 DAY,
                    now(),
                    now());


            WHILE j <= 1000000 DO

                    INSERT INTO coupon_issue (user_id, coupon_id, coupon_name, discount_type, discount_value, is_used, expired_at,
                                        issued_at)
                    VALUES (j,
                            i,
                            CONCAT('쿠폰', i),
                            'FIXED',
                            50000,
                            false,
                            now() + INTERVAL 7 DAY,
                            now());

                    SET j = j + 1;

                END WHILE;

            SET j = 1;
            SET i = i + 1;
        END WHILE;

    WHILE i <= 2
        DO
            INSERT INTO coupon (id, coupon_name, count, discount_type, discount_value, valid_from, valid_to,
                                created_at, updated_at)
            VALUES (i,
                    CONCAT('쿠폰', i),
                    1000000,
                    'RATE',
                    50,
                    now() - INTERVAL 1 DAY,
                    now() + INTERVAL 7 DAY,
                    now(),
                    now());

            WHILE j <= 1000000 DO

                    INSERT INTO coupon_issue (user_id, coupon_id, coupon_name, discount_type, discount_value, is_used, expired_at,
                                              issued_at)
                    VALUES (j,
                            i,
                            CONCAT('쿠폰', i),
                            'RATE',
                            50,
                            false,
                            now() + INTERVAL 7 DAY,
                            now());

                    SET j = j + 1;

                END WHILE;

            SET j = 1;
            SET i = i + 1;
        END WHILE;
END $$

DELIMITER $$

CALL insertDummyCoupons;
