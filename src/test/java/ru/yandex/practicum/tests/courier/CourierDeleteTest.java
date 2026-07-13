package ru.yandex.practicum.tests.courier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tests.dto.CourierDto;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@DisplayName("Тесты удаления курьера")
public class CourierDeleteTest extends ru.yandex.practicum.tests.BaseApiTest {

    private Integer courierId;

    @BeforeEach
    void createCourier() {
        CourierDto courier = new CourierDto();
        courier.setLogin("delete" + System.currentTimeMillis());
        courier.setPassword("12345");
        courier.setFirstName("DeleteMe");

        Integer id = given()
                .spec(requestSpec)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .extract().jsonPath().getInt("id");

        courierId = id;
    }

    @Test
    @DisplayName("Успешное удаление курьера")
    void deleteCourierSuccess() {
        given()
                .spec(requestSpec)
                .pathParam("id", courierId)
                .when()
                .delete("/api/v1/courier/{id}")
                .then()
                .statusCode(200)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Удаление без id — ошибка")
    void deleteCourierMissingId() {
        given()
                .spec(requestSpec)
                .when()
                .delete("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Удаление несуществующего курьера")
    void deleteNonExistingCourier() {
        given()
                .spec(requestSpec)
                .pathParam("id", 999999)
                .when()
                .delete("/api/v1/courier/{id}")
                .then()
                .statusCode(404); // или 400 — зависит от бэкенда
    }
}
