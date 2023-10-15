package com.teamvoy.teamvoytestasignment.repos;

import com.teamvoy.teamvoytestasignment.domain.GoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestGoodRepository extends JpaRepository<GoodEntity, Long> {
    Boolean existsByName(String name);
}
