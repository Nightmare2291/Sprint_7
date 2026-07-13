package ru.yandex.practicum.tests.order;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.tests.BaseApiTest;
import ru.yandex.practicum.tests.dto.CourierDto;
import ru.yandex.practicum.tests.dto.OrderCreateRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты создания заказа")
public class OrderCreateTest extends BaseApiTest {

    private Integer courierIdToDelete;

    @AfterEach
    void cleanup() {
        if (courierIdToDelete != null) {
            given()
                    .spec(requestSpec)
                    .pathParam("id", courierIdToDelete)
                    .delete("/api/v1/courier/{id}")
                    .then()
                    .statusCode(anyOf(is(200), is(404)));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "BLACK,,true",
            "GREY,,true",
            ",BLACK,true",
            ",GREY,true",
            ",,true",
            "BLACK,GREY,true",
            "GREY,BLACK,true"
    })
    void createOrderWithDifferentColors(String color1, String color2, boolean expectedOk) {
        String login = "test" + System.currentTimeMillis();
        prepareCourier(login, "123", "Test");

        List<String> colors = Arrays.asList(
                        color1 == null || color1.isEmpty() ? null : color1,
                        color2 == null || color2.isEmpty() ? null : color2
                ).stream()
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toList());;

        OrderCreateRequest request = OrderCreateRequest.builder()
                .firstName("Test")
                .lastName("Test")
                .address("Test st.")
                .metroStation("Test")
                .phone("+78005553535")
                .rentTime(5)
                .deliveryDate("2024-01-01")
                .comment("Test comment")
                .color(colors.toString())
                .build();

        given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/api/v1/order")
                .then()
                .statusCode(201)
                .body("track", notNullValue())
                .body("ok", is(true));
    }

    private void collect(Collector<Object,?, List<Object>> list) {
    }

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

        courierIdToDelete = extractIdFromLogin(response);
    }

    private Integer extractIdFromLogin(Response response) {
        return response.jsonPath().getInt("id");
    }
}
