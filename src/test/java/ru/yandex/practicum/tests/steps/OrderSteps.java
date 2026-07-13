package ru.yandex.practicum.tests.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.practicum.tests.dto.OrderCreateRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class OrderSteps {

    @Step("Создать заказ")
    public Response createOrder(OrderCreateRequest request) {
        return given()
                .spec(BaseSteps.getRequestSpec())
                .body(request)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .extract().response();
    }

    @Step("Создать заказ и получить track")
    public Integer createOrderAndGetTrack(OrderCreateRequest request) {
        Response response = createOrder(request);
        return response.jsonPath().getInt("track");
    }

    @Step("Получить заказ по track")
    public Response getOrderTrack(Integer track) {
        return given()
                .spec(BaseSteps.getRequestSpec())
                .queryParam("t", track)
                .when()
                .get("/api/v1/orders/track")
                .then()
                .statusCode(200)
                .extract().response();
    }

    @Step("Получить заказ без параметра t")
    public void getOrderTrackWithoutParam() {
        given()
                .spec(BaseSteps.getRequestSpec())
                .when()
                .get("/api/v1/orders/track")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для поиска"));
    }

    @Step("Получить несуществующий заказ")
    public void getOrderTrackNotFound(Integer track) {
        given()
                .spec(BaseSteps.getRequestSpec())
                .queryParam("t", track)
                .when()
                .get("/api/v1/orders/track")
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для поиска"));
    }

    @Step("Принять заказ курьером")
    public void acceptOrder(Integer orderId, Integer courierId) {
        given()
                .spec(BaseSteps.getRequestSpec())
                .pathParam("id", orderId)
                .queryParam("courierId", courierId)
                .when()
                .put("/api/v1/orders/accept/{id}")
                .then()
                .statusCode(200)
                .body("ok", is(true));
    }

    @Step("Принять заказ без id")
    public void acceptOrderWithoutId(Integer courierId) {
        given()
                .spec(BaseSteps.getRequestSpec())
                .queryParam("courierId", courierId)
                .when()
                .put("/api/v1/orders/accept")
                .then()
                .statusCode(400);
    }

    @Step("Получить список заказов")
    public Response getOrdersList() {
        return given()
                .spec(BaseSteps.getRequestSpec())
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .extract().response();
    }
}
