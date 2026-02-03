package org.example.utils.allureReporter;

import io.qameta.allure.Attachment;
import io.restassured.response.Response;
import org.example.constants.Headers;
import org.example.requests.services.CommonService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AllureReporter {

    private final DateTimeFormatter timeFormatter;

    public AllureReporter() {
        this.timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public AllureReporter(DateTimeFormatter timeFormatter) {
        this.timeFormatter = timeFormatter;
    }

    @Attachment(value = "{attachmentName}", type = "text/plain")
    public String reportRequestResponse(CommonService request, String body, Response response) {
        return reportRequestResponse(request, null, body, response);
    }

    @Attachment(value = "{attachmentName}", type = "text/plain")
    public String reportRequestResponse(CommonService request, Map<String, String> headers, String body, Response response) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ТЕСТОВЫЕ ДАННЫЕ ===\n");
        sb.append("Время: ").append(LocalDateTime.now().format(timeFormatter)).append("\n");
        sb.append("Токен: ").append(extractTokenFromRequest(body)).append("\n");
        sb.append("Действие: ").append(extractActionFromRequest(body)).append("\n\n");

        sb.append("=== ЗАПРОС ===\n");
        sb.append("Метод: POST\n");
        sb.append("URL: ").append(request.getLastRequestUrl()).append("\n");
        sb.append("Заголовки:\n");
        if (headers == null) {
            sb.append("- X-API-KEY: отсутствует\n");
        } else if (headers.containsKey("X-Api-Key")) {
            sb.append("- X-API-KEY: ").append(headers.get("X-Api-Key")).append("\n");
        } else {
            sb.append("- X-API-KEY: отсутствует\n");
        }
        sb.append("Тело запроса: ").append(body).append("\n\n");

        sb.append("=== ОТВЕТ ===\n");
        sb.append("Статус код: ").append(response.getStatusCode()).append("\n");
        sb.append("Тело ответа: ").append(response.getBody().asString()).append("\n");
        sb.append("Время ответа: ").append(response.getTime()).append("ms\n");

        return sb.toString();
    }

    @Attachment(value = "URL запроса - {url}", type = "text/plain")
    public String reportUrl(String url, String method) {
        return String.format("URL: %s\nМетод: %s\nВремя: %s",
                url, method, LocalDateTime.now().format(timeFormatter));
    }

    @Attachment(value = "Ошибка - {errorName}", type = "text/plain")
    public String reportError(String errorName, String errorMessage) {
        return String.format("Ошибка: %s\nСообщение: %s\nВремя: %s",
                errorName, errorMessage, LocalDateTime.now().format(timeFormatter));
    }

    private String extractTokenFromRequest(String requestData) {
        if (requestData == null || requestData.isEmpty()) {
            return "Не указан";
        }
        String[] tokenParts = requestData.split("token=");
        if (tokenParts.length < 2) {
            return "Не найден";
        }
        String[] actionParts = tokenParts[1].split("&action=");
        return actionParts.length > 0 ? actionParts[0] : "Не найден";
    }

    private String extractActionFromRequest(String requestData) {
        if (requestData == null || requestData.isEmpty()) {
            return "Не указан";
        }
        String[] actionParts = requestData.split("&action=");
        if (actionParts.length < 2) {
            return "Не найден";
        }
        return actionParts[1];
    }
}