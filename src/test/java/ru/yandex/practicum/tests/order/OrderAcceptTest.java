package ru.yandex.practicum.tests.order;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tests.BaseApiTest;
import ru.yandex.practicum.tests.dto.CourierDto;
import ru.yandex.practicum.tests.dto.OrderCreateRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@DisplayName("Тесты принятия заказа")
public class OrderAcceptTest extends BaseApiTest {

    private Integer orderId;
    private Integer courierId;

    @BeforeEach
    void prepareData() {
        // 1. Создаем курьера
        CourierDto courier = new CourierDto();
        courier.setLogin("accept" + System.currentTimeMillis());
        courier.setPassword("12345");
        courier.setFirstName("AcceptMe");
        courierId = given()
                .spec(requestSpec)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .extract().jsonPath().getInt("id");

        // 2. Создаем заказ
        OrderCreateRequest order = new OrderCreateRequest();
        order.setFirstName("Test");
        order.setAddress("Test");
        order.setMetroStation("1");
        order.setPhone("+7 999 000 00 00");
        order.setRentTime(1);
        order.setDeliveryDate("2024-06-01");

        orderId = given()
                .spec(requestSpec)
                .body(order)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .extract().jsonPath().getInt("track");
    }

    @Test
    @DisplayName("Успешное принятие заказа")
    void acceptOrderSuccess() {
        given()
                .spec(requestSpec)
                .pathParam("id", orderId)
                .queryParam("courierId", courierId)
                .when()
                .put("/api/v1/orders/accept/{id}")
                .then()
                .statusCode(200)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Принятие без id заказа")
    void acceptOrderMissingOrderId() {
        given()
                .spec(requestSpec);
                /*.query*/}}