package ru.yandex.practicum.tests.courier;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tests.BaseApiTest;
import ru.yandex.practicum.tests.dto.CourierDto;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@DisplayName("Тесты создания курьера")
public class CourierCreateTest extends BaseApiTest {

    private Integer courierIdToDelete;

    @AfterEach
    void deleteCourierIfExists() {
        if (courierIdToDelete != null) {
            given()
                    .spec(requestSpec)
                    .pathParam("id", courierIdToDelete)
                    .delete("/api/v1/courier/{id}")
                    .then()
                    .statusCode(anyOf(is(200), is(404))); // 404 — уже удалён
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    void createCourierSuccess() {
        CourierDto courier = new CourierDto();
        String login = "test" + System.currentTimeMillis();
        courier.setLogin(login);
        courier.setPassword("12345");
        courier.setFirstName("TestName");

        Response response = given()
                .spec(requestSpec)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", is(true))
                .extract().response();

        courierIdToDelete = extractIdFromResponse(response);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    void createDuplicateCourierFails() {
        String login = "unique" + System.currentTimeMillis();

        createCourierAndSaveId("unique", "123", "Test");

        CourierDto dup = new CourierDto();
        dup.setLogin(login);
        dup.setPassword("123");
        dup.setFirstName("Test");

        given()
                .spec(requestSpec)
                .body(dup)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", is("Этот логин уже занят"));
    }

    @Test
    @DisplayName("Запрос без обязательных полей возвращает ошибку 400")
    void createCourierMissingFieldsFails() {
        CourierDto courier = new CourierDto();
        courier.setLogin("test");
        courier.setPassword("1234");
        // firstName missing

        given()
                .spec(requestSpec)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }

    @Step("Создать курьера и сохранить его id")
    private void createCourierAndSaveId(String login, String password, String firstName) {
        CourierDto courier = CourierDto.builder()
                .login(login)
                .password(password)
                .firstName(firstName)
                .build();

        Response response = given()
                .spec(requestSpec)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .extract().response();

        courierIdToDelete = extractIdFromResponse(response);
    }

    @Step("Извлечь id из ответа")
    private Integer extractIdFromResponse(Response response) {
        // Учитываем, что API может не возвращать id при создании — тогда вернём null
        Integer id = response.jsonPath().get("id");
        return id != null ? id : -1; // или укажи логику по API (если id в response, например, как в документации)
    }
}
