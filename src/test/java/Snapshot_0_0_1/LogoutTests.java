package Snapshot_0_0_1;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.Validations.CommonServiceValidations;
import org.example.constants.Actions;
import org.example.constants.Headers;
import org.example.requests.services.CommonService;
import org.example.utils.TokenGenerator;
import org.example.utils.allureReporter.AllureReporter;
import org.example.utils.wireMock.WireMockConstants;
import org.example.utils.wireMock.WireMockUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@Slf4j
@Tag("Regress")
@Epic("Logout")
@DisplayName("Тестирование выхода из учетной записи")
public class LogoutTests {

    CommonService request = new CommonService();
    CommonServiceValidations validations = new CommonServiceValidations();
    AllureReporter reporter = new AllureReporter();
    Response response;
    String token;

    @BeforeAll
    static void setUp() {
        WireMockUtil.getInstance(WireMockConstants.PORT_MOCK);
    }

    @BeforeEach
    void reset() {
        WireMockUtil.resetAll();
        token = TokenGenerator.generateToken();
    }

    @AfterEach
    void tearDown() {
        step("Выход из учетной записи");
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGOUT);
    }

    @Step("{description}")
    protected void step(String description) {
        Allure.step(description);
        log.info("Шаг {}", description);
    }


    @Test
    @DisplayName("Успешный выход из учетной записи")
    public void successLogout() {
        step("Аутентификация с валидным токеном и действием \"" + Actions.LOGIN + "\"");
        WireMockUtil.stubPost(WireMockConstants.LOGIN_ENDPOINT_MOCK, WireMockConstants.ACCESS_STATUS_MOCK,
                WireMockConstants.BODY_SUCCESS_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация с валидным токеном и действием", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN, response));

        step("Успешный выход из учетной записи");
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGOUT);
        Allure.addAttachment("Успешный выход из учетной записи", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGOUT, response));

        validations.successLogout(response);
    }


    @Test
    @DisplayName("Повторный выход из учетной записи")
    public void repeatedLogout() {
        step("Аутентификация с валидным токеном и действием \"" + Actions.LOGIN + "\"");
        WireMockUtil.stubPost(WireMockConstants.LOGIN_ENDPOINT_MOCK, WireMockConstants.ACCESS_STATUS_MOCK,
                WireMockConstants.BODY_SUCCESS_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация с валидным токеном и действием", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN, response));

        step("Успешный выход из учетной записи");
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGOUT);
        Allure.addAttachment("Успешный выход из учетной записи", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGOUT, response));

        step("Повторный выход из учетной записи");
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGOUT);
        Allure.addAttachment("Повторный выход из учетной записи", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGOUT, response));

        validations.repeatedLogout(response);
    }


    @ParameterizedTest
    @DisplayName("Выход из учетной записи с невалидным токеном")
    @MethodSource("invalidTokensProvider")
    public void invalidTokenLogout(String description, String methodToken) {
        step("Аутентификация с валидным токеном и действием \"" + Actions.LOGIN + "\"");
        WireMockUtil.stubPost(WireMockConstants.LOGIN_ENDPOINT_MOCK, WireMockConstants.ACCESS_STATUS_MOCK,
                WireMockConstants.BODY_SUCCESS_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация с валидным токеном и действием", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN, response));

        step(description);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + methodToken + "&action=" + Actions.LOGOUT);
        Allure.addAttachment(description, reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + methodToken + "&action=" + Actions.LOGIN, response));

        validations.invalidToken(response);
    }

    static Stream<Arguments> invalidTokensProvider() {
        return Stream.of(
                Arguments.of("пустой токен", ""),
                Arguments.of("токен с буквой в нижнем регистре", "6448817BFA4DC83D4A44BC6DB8B9746a"),
                Arguments.of("токен с буквой, не входящей в диапозон A-F", "6448817BFA4DC83D4A44BC6DB8B9746G"),
                Arguments.of("токен из 31 символа", "6448817BFA4DC83D4A44BC6DB8B9746"),
                Arguments.of("токен из 33 символов", "6448817BFA4DC83D4A44BC6DB8B97467A"));
    }


    @ParameterizedTest
    @DisplayName("Выход из учетной записи с недопустимым параметром \"action\"")
    @MethodSource("invalidActionLogout")
    public void invalidActionLogout(String description, String methodAction) {
        step("Аутентификация с валидным токеном и действием \"" + Actions.LOGIN + "\"");
        WireMockUtil.stubPost(WireMockConstants.LOGIN_ENDPOINT_MOCK, WireMockConstants.ACCESS_STATUS_MOCK,
                WireMockConstants.BODY_SUCCESS_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация с валидным токеном и действием", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN, response));

        step(description);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + methodAction);
        Allure.addAttachment(description, reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + methodAction, response));

        validations.invalidAction(response);
    }

    static Stream<Arguments> invalidActionLogout() {
        return Stream.of(
                Arguments.of("С пустым действием", ""),
                Arguments.of("С недопустимым действием", Actions.REGISTER)
        );
    }
}
