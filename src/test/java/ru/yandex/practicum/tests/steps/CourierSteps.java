package ru.yandex.practicum.tests.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.practicum.tests.dto.CourierDto;
import ru.yandex.practicum.tests.dto.CourierLoginRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

public class CourierSteps {

    @Step("Создать курьера")
    public Response createCourier(CourierDto courier) {
        return given()
                .spec(BaseSteps.getRequestSpec())
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .extract().response();
    }

    @Step("Создать курьера и получить id")
    public Integer createCourierAndGetId(CourierDto courier) {
        Response response = createCourier(courier);
        return response.jsonPath().getInt("id");
    }

    @Step("Удалить курьера по id")
    public void deleteCourierById(Integer id) {
        given()
                .spec(BaseSteps.getRequestSpec())
                .pathParam("id", id)
                .when()
                .delete("/api/v1/courier/{id}")
                .then()
                .statusCode(anyOf(is(200), is(404)));
    }

    @Step("Попытка удалить курьера без id")
    public void deleteCourierWithoutId() {
        given()
                .spec(BaseSteps.getRequestSpec())
                .when()
                .delete("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для удаления курьера"));
    }

    @Step("Попытка удалить несуществующего курьера")
    public void deleteNonExistingCourier(Integer id) {
        given()
                .spec(BaseSteps.getRequestSpec())
                .pathParam("id", id)
                .when()
                .delete("/api/v1/courier/{id}")
                .then()
                .statusCode(404);
    }

    @Step("Авторизация курьера")
    public Response loginCourier(CourierLoginRequest request) {
        return given()
                .spec(BaseSteps.getRequestSpec())
                .body(request)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract().response();
    }

    @Step("Авторизация курьера с неверным паролем")
    public void loginCourierWithInvalidPassword(CourierLoginRequest request) {
        given()
                .spec(BaseSteps.getRequestSpec())
                .body(request)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", is("Учетная запись не найдена"));
    }

    @Step("Авторизация курьера без пароля")
    public void loginCourierWithoutPassword(CourierLoginRequest request) {
        given()
                .spec(BaseSteps.getRequestSpec())
                .body(request)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Step("Авторизация несуществующего курьера")
    public void loginNonExistingCourier(CourierLoginRequest request) {
        given()
                .spec(BaseSteps.getRequestSpec())
                .body(request)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", is("Учетная запись не найдена"));
    }

    @Step("Попытка создания дубликата курьера")
    public void createDuplicateCourier(CourierDto courier) {
        given()
                .spec(BaseSteps.getRequestSpec())
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", is("Этот логин уже занят"));
    }

    @Step("Создать курьера без обязательных полей")
    public void createCourierWithMissingFields(CourierDto courier) {
        given()
                .spec(BaseSteps.getRequestSpec())
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }
}
