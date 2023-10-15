package com.teamvoy.teamvoytestasignment.integration;

import com.teamvoy.teamvoytestasignment.domain.GoodEntity;
import com.teamvoy.teamvoytestasignment.dto.GoodDto;
import com.teamvoy.teamvoytestasignment.dto.CreateGoodDto;
import com.teamvoy.teamvoytestasignment.exceptions.models.ValidationErrorResponseDto;
import com.teamvoy.teamvoytestasignment.utils.DataGenerateUtils;
import com.teamvoy.teamvoytestasignment.repos.TestGoodRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class GoodControllerTest {
    @Autowired
    private TestGoodRepository goodRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public void before() {
        goodRepository.deleteAll();
    }

    @Test
    public void testCreateGood_Positive() {
        CreateGoodDto createGoodDto = new CreateGoodDto("Test Good", 10.0, 100);

        ResponseEntity<GoodDto> response = restTemplate.postForEntity("/goods", createGoodDto, GoodDto.class);
        GoodDto createdGood = response.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertNotNull(createdGood),
                () -> assertNotNull(createdGood.getId()),
                () -> assertEquals(createGoodDto.getName(), createdGood.getName()),
                () -> assertEquals(createGoodDto.getPrice(), createdGood.getPrice()),
                () -> assertEquals(createGoodDto.getQuantity(), createdGood.getQuantity()),
                () -> assertEquals(1, goodRepository.findAll().size()),
                () -> assertTrue(goodRepository.existsByName(createdGood.getName()))
        );
    }

    @Test
    public void testCreateGood_Negative() {
        CreateGoodDto createGoodDto = new CreateGoodDto("T", -10.0, -200);

        ResponseEntity<ValidationErrorResponseDto[]> response = restTemplate.postForEntity("/goods", createGoodDto, ValidationErrorResponseDto[].class);
        ValidationErrorResponseDto[] errors = response.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertEquals(3, errors.length)
        );
    }


    @Test
    public void testCreateGood_NegativeDuplicate() {
        GoodEntity good = new GoodEntity(1L, "Test Good", 10.0, 100, LocalDateTime.now());
        restTemplate.getRestTemplate().postForEntity("/goods", good, GoodDto.class);

        ResponseEntity<GoodDto> response = restTemplate.postForEntity("/goods", good, GoodDto.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testUpdateGood_Positive() {
        GoodEntity good = goodRepository.save(GoodEntity.builder().name("Test good").price(10.0).quantity(100).created(LocalDateTime.now()).build());
        CreateGoodDto createGoodDto = new CreateGoodDto("Good test", 20.0, 200);


        ResponseEntity<GoodDto> response = restTemplate.exchange("/goods/" + good.getId(), HttpMethod.PUT, new HttpEntity<>(createGoodDto), GoodDto.class);
        GoodDto updatedGood = response.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(updatedGood),
                () -> assertNotNull(updatedGood.getId()),
                () -> assertEquals(createGoodDto.getName(), updatedGood.getName()),
                () -> assertEquals(createGoodDto.getPrice(), updatedGood.getPrice()),
                () -> assertEquals(createGoodDto.getQuantity(), updatedGood.getQuantity())
        );
    }

    @Test
    public void testUpdateGood_Negative() {
        CreateGoodDto createGoodDto = new CreateGoodDto("Test Good 2", 20.0, 200);

        ResponseEntity<GoodDto> response = restTemplate.exchange("/goods/" + 100L, HttpMethod.PUT, new HttpEntity<>(createGoodDto), GoodDto.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    public void testFindAll() {
        List<GoodEntity> goodEntityList = DataGenerateUtils.generateRandomGoods(10);
        goodRepository.saveAll(goodEntityList);

        ResponseEntity<GoodDto[]> response = restTemplate.getForEntity("/goods", GoodDto[].class);
        GoodDto[] goods = response.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(goods),
                () -> assertNotEquals(0, goods.length),
                () -> assertEquals(goodEntityList.size(), goods.length),
                () -> assertEquals(goods.length, goodRepository.findAll().size())
        );
    }
}
