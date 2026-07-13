package ru.yandex.practicum.tests.steps;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

import static io.restassured.http.ContentType.JSON;

public class BaseSteps {

    private static RequestSpecification requestSpec;

    public static RequestSpecification getRequestSpec() {
        if (requestSpec == null) {
            requestSpec = new RequestSpecBuilder()
                    .setBaseUri("https://qa-scooter.praktikum-services.ru")
                    .setContentType(JSON)
                    .log(LogDetail.ALL)
                    .build();
        }
        return requestSpec;
    }
}
