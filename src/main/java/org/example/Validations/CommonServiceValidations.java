package org.example.Validations;

import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CommonServiceValidations extends ValidationBase {

    public void successLogin(Response response) {
        step("Проверка статус кода, полученного в ответе");
        assertEquals(200, response.getStatusCode());

        assertNotBlank(response.getBody().asString());
    }

    public void wrongX_API_KEYOrToken(Response response) {
        step("Проверка статус кода, полученного в ответе");
        assertEquals(401, response.getStatusCode());

        assertNotBlank(response.getBody().asString());
    }

    public void invalidAction(Response response) {
        step("Проверка статус кода, полученного в ответе");
        assertEquals(400, response.getStatusCode());

        assertNotBlank(response.getBody().asString());
    }

    public void wrongAction(Response response) {
        step("Проверка статус кода, полученного в ответе");
        assertEquals(403, response.getStatusCode());

        assertNotBlank(response.getBody().asString());
    }

    public void successAction(Response response) {
        step("Проверка статус кода, полученного в ответе");
        assertEquals(200, response.getStatusCode());

        assertNotBlank(response.getBody().asString());
    }

    public void invalidToken(Response response) {
        step("Проверка статус кода, полученного в ответе");
        assertEquals(400, response.getStatusCode());

        assertNotBlank(response.getBody().asString());
    }

    public void unavailableTokenInAction(Response response) {
        step("Проверка статус кода, полученного в ответе");
        assertEquals(403, response.getStatusCode());

        assertNotBlank(response.getBody().asString());
    }

    public void successLogout(Response response) {
        step("Проверка статус кода, полученного в ответе");
        assertEquals(200, response.getStatusCode());

        assertNotBlank(response.getBody().asString());
    }

    public void repeatedLogout(Response response) {
        step("Проверка статус кода, полученного в ответе");
        assertEquals(403, response.getStatusCode());

        assertNotBlank(response.getBody().asString());
    }
}
