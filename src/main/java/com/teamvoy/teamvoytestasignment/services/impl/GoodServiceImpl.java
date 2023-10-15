package com.teamvoy.teamvoytestasignment.services.impl;

import com.teamvoy.teamvoytestasignment.domain.GoodEntity;
import com.teamvoy.teamvoytestasignment.dto.ChangeAmountDto;
import com.teamvoy.teamvoytestasignment.dto.GoodDto;
import com.teamvoy.teamvoytestasignment.dto.CreateGoodDto;
import com.teamvoy.teamvoytestasignment.exceptions.models.ResourceDuplicateException;
import com.teamvoy.teamvoytestasignment.exceptions.models.ResourceNotFoundException;
import com.teamvoy.teamvoytestasignment.mappers.BusinessMapper;
import com.teamvoy.teamvoytestasignment.repositories.GoodRepository;
import com.teamvoy.teamvoytestasignment.services.GoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoodServiceImpl implements GoodService {
    private final GoodRepository goodRepository;

    @Override
    public GoodDto createGood(CreateGoodDto createGoodDto) {
        if (goodRepository.existsByName(createGoodDto.getName())) {
            throw new ResourceDuplicateException(String.format("Good with name: %s already exist", createGoodDto.getName()));
        }
        GoodEntity good = GoodEntity.builder()
                .created(LocalDateTime.now())
                .price(createGoodDto.getPrice())
                .name(createGoodDto.getName())
                .quantity(createGoodDto.getQuantity())
                .build();
        return BusinessMapper.INSTANCE.goodEntityToDto(goodRepository.save(good));
    }

    @Override
    public GoodDto updateGood(Long id, CreateGoodDto createGoodDto) {
        GoodEntity good = findGoodById(id);
        BeanUtils.copyProperties(createGoodDto, good);
        return BusinessMapper.INSTANCE.goodEntityToDto(goodRepository.save(good));
    }

    @Override
    public List<GoodDto> findAll() {
        return BusinessMapper.INSTANCE.goodsToDto(goodRepository.findAll());
    }

    @Override
    public GoodEntity findGoodById(Long goodId) {
        return goodRepository.findById(goodId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Good with id: %s is not found", goodId)));
    }

    @Override
    public Boolean checkAvailability(Long goodId, Integer quantity) {
        return findGoodById(goodId).getQuantity() >= quantity;
    }

    @Override
    public void changeAmounts(List<ChangeAmountDto> changeAmounts) {
        Map<Long, Integer> changeAmountMap = changeAmounts.stream().collect(Collectors.toMap(ChangeAmountDto::getGoodId, ChangeAmountDto::getAmount));
        List<GoodEntity> goods = goodRepository.findAllById(changeAmountMap.keySet());
        goods.forEach(good -> {
            int changeAmount = changeAmountMap.getOrDefault(good.getId(), 0);
            int newQuantity = good.getQuantity() - changeAmount;
            good.setQuantity(newQuantity);
        });
        goodRepository.saveAll(goods);
    }
}
