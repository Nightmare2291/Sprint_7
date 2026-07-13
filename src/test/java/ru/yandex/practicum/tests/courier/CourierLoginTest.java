package ru.yandex.practicum.tests.courier;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.tests.BaseApiTest;
import ru.yandex.practicum.tests.dto.CourierDto;
import ru.yandex.practicum.tests.dto.CourierLoginRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты авторизации курьера")
public class CourierLoginTest extends BaseApiTest {

    private String loginToDelete;
    private Integer courierIdToDelete;

    @AfterEach
    void cleanup() {
        if (courierIdToDelete != null && loginToDelete != null) {
            given()
                    .spec(requestSpec)
                    .pathParam("id", courierIdToDelete)
                    .delete("/api/v1/courier/{id}")
                    .then()
                    .statusCode(anyOf(is(200), is(404)));
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    void loginSuccess() {
        String login = "test" + System.currentTimeMillis();
        String password = "12345";
        String firstName = "Test";

        prepareCourier(login, password, firstName);

        CourierLoginRequest req = CourierLoginRequest.builder()
                .login(login)
                .password(password)
                .build();

        given()
                .spec(requestSpec)
                .body(req)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Ошибка при неверном пароле")
    void loginInvalidCredentials() {
        String login = "test" + System.currentTimeMillis();
        String password = "12345";
        String firstName = "Test";

        prepareCourier(login, password, firstName);

        CourierLoginRequest req = CourierLoginRequest.builder()
                .login(login)
                .password("wrongpass")
                .build();

        given()
                .spec(requestSpec)
                .body(req)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", is("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка при отсутствии поля password")
    void loginMissingPassword() {
        String login = "test" + System.currentTimeMillis();
        prepareCourier(login, "123", "Test");

        CourierLoginRequest req = CourierLoginRequest.builder()
                .login(login)
                .password(null)
                .build();

        given()
                .spec(requestSpec)
                .body(req)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка при попытке войти под несуществующим курьером")
    void loginNonExistentCourier() {
        CourierLoginRequest req = CourierLoginRequest.builder()
                .login("nonexistent")
                .password("12345")
                .build();

        given()
                .spec(requestSpec)
                .body(req)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", is("Учетная запись не найдена"));
    }

    @Step("Создать курьера и получить его id")
    private void prepareCourier(String login, String password, String firstName) {
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

        loginToDelete = login;
        courierIdToDelete = extractIdFromLogin(response);
    }

    private Integer extractIdFromLogin(Response response) {
        return response.jsonPath().getInt("id");
    }
}
