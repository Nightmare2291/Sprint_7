package ru.yandex.practicum.tests.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tests.BaseApiTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты получения списка заказов")
public class OrdersListTest extends BaseApiTest {

    @Test
    @DisplayName("Получение списка заказов возвращает список")
    void getOrdersList() {
        given()
                .spec(requestSpec)
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", not(empty()))
                .body("orders.id", notNullValue())
                .body("pageInfo.page", notNullValue())
                .body("pageInfo.total", notNullValue())
                .body("pageInfo.limit", notNullValue());
    }
}