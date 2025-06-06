DELIMITER $$

DROP PROCEDURE IF EXISTS insertDummyUsers$$

CREATE PROCEDURE insertDummyUsers()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 1000000
        DO
            INSERT INTO users (id, login_id, password, created_at, updated_at)
            VALUES (i,
                    CONCAT('user', i),
                    'password',
                    now(),
                    now());
            SET i = i + 1;
END WHILE;
END $$

DELIMITER $$

CALL insertDummyUsers;