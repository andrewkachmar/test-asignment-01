package com.teamvoy.teamvoytestasignment.repositories;

import com.teamvoy.teamvoytestasignment.domain.OrderEntity;
import com.teamvoy.teamvoytestasignment.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
