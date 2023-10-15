package com.teamvoy.teamvoytestasignment.repositories;

import com.teamvoy.teamvoytestasignment.domain.GoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodRepository extends JpaRepository<GoodEntity, Long> {
    Boolean existsByName(String name);
}
