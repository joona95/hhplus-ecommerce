DELIMITER $$

DROP PROCEDURE IF EXISTS insertDummyItems$$

CREATE PROCEDURE insertDummyItems()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 1000000
        DO
            INSERT INTO item (id, item_name, count, price, created_at, updated_at)
            VALUES (i,
                    CONCAT('상품', i),
                    1000000,
                    10000,
                    now(),
                    now());
            SET i = i + 1;
        END WHILE;
END $$

DELIMITER $$

CALL insertDummyItems;