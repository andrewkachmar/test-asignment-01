package com.teamvoy.teamvoytestasignment.mappers;

import com.teamvoy.teamvoytestasignment.domain.GoodEntity;
import com.teamvoy.teamvoytestasignment.domain.OrderEntity;
import com.teamvoy.teamvoytestasignment.dto.GoodDto;
import com.teamvoy.teamvoytestasignment.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BusinessMapper {
    BusinessMapper INSTANCE = Mappers.getMapper(BusinessMapper.class);

    GoodDto goodEntityToDto(GoodEntity good);

    List<GoodDto> goodsToDto(List<GoodEntity> goods);

    OrderDto orderToDto(OrderEntity orderEntity);

    @Mapping(target="good", source="good")
    List<OrderDto> ordersToDto(List<OrderEntity> orders);
}
