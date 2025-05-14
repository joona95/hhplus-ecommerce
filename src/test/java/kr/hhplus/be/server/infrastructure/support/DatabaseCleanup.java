package kr.hhplus.be.server.infrastructure.support;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleanup {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void truncateAllTables() {

        entityManager.createNativeQuery("TRUNCATE TABLE users").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE point").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE point_history").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE coupon").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE coupon_issue").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE item").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE popular_item_statistics").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE orders").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE order_item").executeUpdate();
    }
}
