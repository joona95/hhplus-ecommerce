DELIMITER $$

DROP PROCEDURE IF EXISTS insertDummyPoints$$

CREATE PROCEDURE insertDummyPoints()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 1000000
        DO
            INSERT INTO point (id, user_id, value, version, updated_at)
            VALUES (i,
                    i,
                    1000000,
                    1,
                    now());
            SET i = i + 1;
END WHILE;
END $$

DELIMITER $$

CALL insertDummyPoints;