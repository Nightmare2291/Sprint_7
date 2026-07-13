package ru.yandex.practicum.tests.order;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tests.BaseApiTest;
import ru.yandex.practicum.tests.dto.OrderCreateRequest;
import ru.yandex.practicum.tests.dto.OrderGetResponse;
import ru.yandex.practicum.tests.utils.TestCleanup;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Тесты получения заказа по треку")
public class OrderGetTest extends BaseApiTest {

    private final List<Integer> createdCourierIds = new ArrayList<>();
    private final List<Integer> createdOrderTracks = new ArrayList<>();

    @BeforeEach
    void cleanData() {
        TestCleanup.deleteCourierByIds(createdCourierIds);
        TestCleanup.deleteOrderTracks(createdOrderTracks);
        createdCourierIds.clear();
        createdOrderTracks.clear();
    }

    @AfterEach
    void tearDown() {
        TestCleanup.deleteCourierByIds(createdCourierIds);
        TestCleanup.deleteOrderTracks(createdOrderTracks);
        createdCourierIds.clear();
        createdOrderTracks.clear();
    }

    @Test
    @Description("Успешное получение заказа по треку")
    void getOrderTrackSuccess() {
        // 1. Создаём заказ
        OrderCreateRequest order = new OrderCreateRequest();
        order.setFirstName("Naruto");
        order.setLastName("Uzumaki");
        order.setAddress("Konoha");
        order.setMetroStation("1");
        order.setPhone("+7 800 555 35 35");
        order.setRentTime(5);
        order.setDeliveryDate("2024-06-21");
        order.setComment("Come back!");

        Response createResponse = given()
                .spec(requestSpec)
                .body(order)
                .when()
                .post("/api/v1/orders");

        assertEquals(201, createResponse.statusCode(), "Ожидается статус 201 Created");
        Integer track = createResponse.jsonPath().getInt("track");
        assertNotNull(track, "Ответ должен содержать track");
        createdOrderTracks.add(track);

        // 2. Получаем заказ по треку
        Response getResponse = given()
                .spec(requestSpec)
                .queryParam("t", track)
                .when()
                .get("/api/v1/orders/track");

        assertEquals(200, getResponse.statusCode(), "Ожидается статус 200 OK");
        OrderGetResponse responseDto = mapToOrderResponse(getResponse);

        assertEquals(track, responseDto.getTrack(), "track в ответе должен совпадать");
        assertEquals("Naruto", responseDto.getFirstName(), "Имя в ответе должно совпадать");
    }

    @Test
    @Description("Получение заказа без параметра t — ошибка 400")
    void getOrderTrackMissingTrackParam() {
        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1/orders/track")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для поиска"));
    }

    @Test
    @Description("Получение несуществующего заказа — ошибка 400")
    void getOrderTrackNotFound() {
        given()
                .spec(requestSpec)
                .queryParam("t", 999999)
                .when()
                .get("/api/v1/orders/track")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для поиска"));
    }

    // Маппинг тела ответа в DTO (без Lombok)
    private OrderGetResponse mapToOrderResponse(Response response) {
        OrderGetResponse dto = new OrderGetResponse();

        dto.setId(response.jsonPath().getInt("order.id"));
        dto.setCourierId(response.jsonPath().getInt("order.courierId"));
        dto.setFirstName(response.jsonPath().getString("order.firstName"));
        dto.setLastName(response.jsonPath().getString("order.lastName"));
        dto.setAddress(response.jsonPath().getString("order.address"));
        dto.setMetroStation(response.jsonPath().getString("order.metroStation"));
        dto.setPhone(response.jsonPath().getString("order.phone"));
        dto.setRentTime(response.jsonPath().getInt("order.rentTime"));
        dto.setDeliveryDate(response.jsonPath().getString("order.deliveryDate"));
        dto.setTrack(response.jsonPath().getInt("order.track"));
        dto.setColor(response.jsonPath().getList("order.color", String.class));
        dto.setComment(response.jsonPath().getString("order.comment"));
        dto.setCreatedAt(response.jsonPath().getString("order.createdAt"));
        dto.setUpdatedAt(response.jsonPath().getString("order.updatedAt"));
        dto.setStatus(response.jsonPath().getInt("order.status"));
        dto.setCancelled(response.jsonPath().getBoolean("order.cancelled"));
        dto.setFinished(response.jsonPath().getBoolean("order.finished"));
        dto.setInDelivery(response.jsonPath().getBoolean("order.inDelivery"));
        dto.setCourierFirstName(response.jsonPath().getString("order.courierFirstName"));

        return dto;
    }
}
