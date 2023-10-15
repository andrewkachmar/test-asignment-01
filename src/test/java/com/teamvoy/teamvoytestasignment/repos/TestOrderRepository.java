package com.teamvoy.teamvoytestasignment.repos;

import com.teamvoy.teamvoytestasignment.domain.OrderEntity;
import com.teamvoy.teamvoytestasignment.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestOrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByOrderStatus(OrderStatus orderStatus);
}
