package com.teamvoy.teamvoytestasignment.unit;

import com.teamvoy.teamvoytestasignment.domain.GoodEntity;
import com.teamvoy.teamvoytestasignment.dto.ChangeAmountDto;
import com.teamvoy.teamvoytestasignment.dto.GoodDto;
import com.teamvoy.teamvoytestasignment.dto.CreateGoodDto;
import com.teamvoy.teamvoytestasignment.exceptions.models.ResourceDuplicateException;
import com.teamvoy.teamvoytestasignment.exceptions.models.ResourceNotFoundException;
import com.teamvoy.teamvoytestasignment.repositories.GoodRepository;
import com.teamvoy.teamvoytestasignment.services.impl.GoodServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.teamvoy.teamvoytestasignment.utils.DataGenerateUtils.generateRandomGoods;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoodServiceTest {
    @InjectMocks
    private GoodServiceImpl goodService;
    @Mock
    private GoodRepository goodRepository;


    @Test
    public void createGood_Success() {
        CreateGoodDto createGoodDto = new CreateGoodDto("iPhone 13 Pro", 10.0, 100);
        when(goodRepository.existsByName("iPhone 13 Pro")).thenReturn(false);
        when(goodRepository.save(any(GoodEntity.class))).thenReturn(new GoodEntity());

        GoodDto result = goodService.createGood(createGoodDto);

        assertNotNull(result);

        verify(goodRepository, times(1)).existsByName("iPhone 13 Pro");
        verify(goodRepository, times(1)).save(any(GoodEntity.class));
    }

    @Test
    public void createGood_Duplicate() {
        CreateGoodDto createGoodDto = new CreateGoodDto("Test Good", 10.0, 100);
        when(goodRepository.existsByName("Test Good")).thenReturn(true);

        assertThrows(ResourceDuplicateException.class, () -> goodService.createGood(createGoodDto));

        verify(goodRepository, times(1)).existsByName("Test Good");
    }

    @Test
    public void updateGood_Success() {
        Long goodId = 1L;
        CreateGoodDto createGoodDto = new CreateGoodDto("Updated Good", 15.0, 200);
        GoodEntity existingGoodEntity = GoodEntity.builder().id(goodId).build();
        GoodEntity updatedGoodEntity = GoodEntity.builder()
                .id(goodId)
                .name(createGoodDto.getName())
                .price(createGoodDto.getPrice())
                .quantity(createGoodDto.getQuantity())
                .build();
        when(goodRepository.findById(goodId)).thenReturn(Optional.of(existingGoodEntity));
        when(goodRepository.save(any(GoodEntity.class))).thenReturn(updatedGoodEntity);

        GoodDto result = goodService.updateGood(goodId, createGoodDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(createGoodDto.getName(), result.getName()),
                () -> assertEquals(createGoodDto.getPrice(), result.getPrice()),
                () -> assertEquals(createGoodDto.getQuantity(), result.getQuantity())
        );

        verify(goodRepository, times(1)).findById(goodId);
        verify(goodRepository, times(1)).save(any(GoodEntity.class));
    }

    @Test
    public void updateGood_NotFound() {
        Long goodId = 1L;
        CreateGoodDto createGoodDto = new CreateGoodDto("Updated Good", 15.0, 200);
        when(goodRepository.findById(goodId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> goodService.updateGood(goodId, createGoodDto));

        verify(goodRepository, times(1)).findById(goodId);
    }


    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 20, 50})
    public void testFindAll(int numberOfGoods) {
        List<GoodEntity> goods = generateRandomGoods(numberOfGoods);
        when(goodRepository.findAll()).thenReturn(goods);

        List<GoodDto> result = goodService.findAll();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(goods.size(), result.size())
        );
        verify(goodRepository, times(1)).findAll();
    }

    @Test
    public void testFindGoodById_Success() {
        Long goodId = 1L;
        GoodEntity goodEntity = GoodEntity.builder().id(goodId).build();
        when(goodRepository.findById(goodId)).thenReturn(java.util.Optional.of(goodEntity));

        GoodEntity resultGood = goodService.findGoodById(goodId);

        assertAll(
                () -> assertNotNull(resultGood),
                () -> assertEquals(goodId, resultGood.getId())
        );
        verify(goodRepository, times(1)).findById(goodId);
    }

    @Test
    public void testFindGoodById_NotFound() {
        Long goodId = 1L;
        when(goodRepository.findById(goodId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> goodService.findGoodById(goodId));

        verify(goodRepository, times(1)).findById(goodId);
    }

    @Test
    public void testCheckAvailability_Available() {
        Long goodId = 1L;
        Integer actualQuantity = 10;
        GoodEntity goodEntity = GoodEntity.builder()
                .id(goodId)
                .quantity(actualQuantity)
                .build();
        when(goodRepository.findById(goodId)).thenReturn(java.util.Optional.of(goodEntity));

        Boolean result = goodService.checkAvailability(goodId, actualQuantity);

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result)
        );
        verify(goodRepository, times(1)).findById(goodId);
    }

    @Test
    public void testCheckAvailability_NotAvailable() {
        Long goodId = 1L;
        Integer actualQuantity = 5;
        Integer expectedQuantity = 10;
        GoodEntity goodEntity = GoodEntity.builder()
                .id(goodId)
                .quantity(actualQuantity)
                .build();
        when(goodRepository.findById(goodId)).thenReturn(java.util.Optional.of(goodEntity));

        Boolean result = goodService.checkAvailability(goodId, expectedQuantity);

        assertFalse(result);

        verify(goodRepository, times(1)).findById(goodId);
    }

    @Test
    public void testChangeAmounts() {
        List<ChangeAmountDto> changeAmounts = List.of(
                new ChangeAmountDto(1L, 5),
                new ChangeAmountDto(2L, 10)
        );
        GoodEntity good1 = GoodEntity.builder()
                .id(1L)
                .quantity(100)
                .build();
        GoodEntity good2 = GoodEntity.builder()
                .id(2L)
                .quantity(50)
                .build();
        List<GoodEntity> goods = List.of(good1, good2);
        when(goodRepository.findAllById(anyIterable())).thenReturn(goods);
        when(goodRepository.saveAll(anyList())).thenReturn(goods);

        goodService.changeAmounts(changeAmounts);

        assertAll(
                () -> assertEquals(95, good1.getQuantity()),
                () -> assertEquals(40, good2.getQuantity())
        );

        verify(goodRepository, times(1)).findAllById(anyIterable());
        verify(goodRepository, times(1)).saveAll(anyList());
    }
}
