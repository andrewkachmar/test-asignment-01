package com.teamvoy.teamvoytestasignment.services;

import com.teamvoy.teamvoytestasignment.domain.GoodEntity;
import com.teamvoy.teamvoytestasignment.dto.ChangeAmountDto;
import com.teamvoy.teamvoytestasignment.dto.GoodDto;
import com.teamvoy.teamvoytestasignment.dto.CreateGoodDto;

import java.util.List;

public interface GoodService {
    GoodDto createGood(CreateGoodDto createGoodDto);

    GoodDto updateGood(Long id, CreateGoodDto createGoodDto);

    List<GoodDto> findAll();

    GoodEntity findGoodById(Long goodId);

    Boolean checkAvailability(Long goodId, Integer quantity);

    void changeAmounts(List<ChangeAmountDto> changeAmountDtos);
}
