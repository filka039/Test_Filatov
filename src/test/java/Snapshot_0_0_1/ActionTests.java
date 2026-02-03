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
@Epic("Action")
@DisplayName("Тестирование действий")
public class ActionTests {

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
    void reset(TestInfo testInfo) {
        WireMockUtil.resetAll();
        token = TokenGenerator.generateToken();
        step("Сгенерирован токен: " + token);
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
    @DisplayName("Успешное действие")
    public void successAction() {
        step("Аутентификация с валидным токеном и действием \"" + Actions.LOGIN + "\"");
        WireMockUtil.stubPost(WireMockConstants.LOGIN_ENDPOINT_MOCK, WireMockConstants.ACCESS_STATUS_MOCK,
                WireMockConstants.BODY_SUCCESS_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация с валидным токеном и действием", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN, response));

        step("Отправка запроса на действие с токеном, прошедшим аутентификацию и помещенным в хранилище");
        WireMockUtil.stubPost(WireMockConstants.ACTION_ENDPOINT_MOCK, WireMockConstants.ACCESS_STATUS_MOCK,
                WireMockConstants.BODY_SUCCESS_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.ACTION);
        Allure.addAttachment("Отправка запроса на действие с токеном, прошедшим аутентификацию и помещенным в хранилище",
                reporter.reportRequestResponse(request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.ACTION,
                        response));

        validations.successAction(response);
    }


    @ParameterizedTest
    @DisplayName("Действие с невалидным токеном")
    @MethodSource("invalidTokensProvider")
    public void invalidTokenLogin(String description, String methodToken) {
        step("Аутентификация с валидным токеном и действием \"" + Actions.LOGIN + "\"");
        WireMockUtil.stubPost(WireMockConstants.LOGIN_ENDPOINT_MOCK, WireMockConstants.ACCESS_STATUS_MOCK,
                WireMockConstants.BODY_SUCCESS_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация с валидным токеном и действием", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN, response));

        step(description);
        WireMockUtil.stubPost(WireMockConstants.ACTION_ENDPOINT_MOCK, WireMockConstants.BAD_REQUEST_STATUS_MOCK,
                WireMockConstants.BODY_ERROR_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + methodToken + "&action=" + Actions.ACTION);
        Allure.addAttachment(description, reporter.reportRequestResponse(request, Headers.getX_API_KEY(),
                "token=" + methodToken + "&action=" + Actions.ACTION, response));

        validations.invalidToken(response);
    }

    static Stream<Arguments> invalidTokensProvider() {
        return Stream.of(
                Arguments.of("пустой токен", ""),
                Arguments.of("токен с буквой в нижнем регистре", "6448817BFA4DC83D4A44BC6DB8B9746a"),
                Arguments.of("токен с буквой, не входящей в диапозон A-F", "6448817BFA4DC83D4A44BC6DB8B9746G"),
                Arguments.of("токен из 31 символа", "6448817BFA4DC83D4A44BC6DB8B9746"),
                Arguments.of("токен из 33 символов", "6448817BFA4DC83D4A44BC6DB8B97467A")
        );
    }


    @Test
    @DisplayName("Действие с недействительным токеном")
    public void unavailableTokenLogin() {
        step("Аутентификация с валидным токеном и действием \"" + Actions.LOGIN + "\"");
        WireMockUtil.stubPost(WireMockConstants.LOGIN_ENDPOINT_MOCK, WireMockConstants.ACCESS_STATUS_MOCK,
                WireMockConstants.BODY_SUCCESS_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация с валидным токеном и действием", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN, response));

        step("Отправка запроса на действие с токеном, непрошедшим аутентификацию и непомещенным в хранилище");
        String newToken = TokenGenerator.generateToken();
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + newToken + "&action=" + Actions.ACTION);
        Allure.addAttachment("Отправка запроса на действие с токеном, непрошедшим аутентификацию и непомещенным в хранилище",
                reporter.reportRequestResponse(request, Headers.getX_API_KEY(), "token=" + newToken + "&action=" +
                        Actions.ACTION, response));

        validations.unavailableTokenInAction(response);
    }


    @ParameterizedTest
    @DisplayName("Действие с недопустимым параметром \"action\"")
    @MethodSource("invalidActionAction")
    public void invalidActionAction(String description, String methodAction) {
        step("Аутентификация с валидным токеном и действием \"" + Actions.LOGIN + "\"");
        WireMockUtil.stubPost(WireMockConstants.LOGIN_ENDPOINT_MOCK, WireMockConstants.ACCESS_STATUS_MOCK,
                WireMockConstants.BODY_SUCCESS_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация с валидным токеном и действием", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN, response));

        step(description);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + methodAction);
        Allure.addAttachment(description, reporter.reportRequestResponse(request, Headers.getX_API_KEY(),
                "token=" + token + "&action=" + methodAction, response));

        validations.invalidAction(response);
    }

    static Stream<Arguments> invalidActionAction() {
        return Stream.of(
                Arguments.of("пустое действие", ""),
                Arguments.of("недопустимое действие", Actions.REGISTER)
        );
    }


    @Test
    @DisplayName("Повторное действие после выхода из учетной записи")
    public void testLogin() {
        step("Аутентификация с валидным токеном и действием \"" + Actions.LOGIN + "\"");
        WireMockUtil.stubPost(WireMockConstants.LOGIN_ENDPOINT_MOCK, WireMockConstants.ACCESS_STATUS_MOCK,
                WireMockConstants.BODY_SUCCESS_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация с валидным токеном и действием", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN, response));

        step("Отправка запроса на действие с токеном, прошедшим аутентификацию и помещенным в хранилище");
        WireMockUtil.stubPost(WireMockConstants.ACTION_ENDPOINT_MOCK, WireMockConstants.ACCESS_STATUS_MOCK,
                WireMockConstants.BODY_SUCCESS_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.ACTION);
        Allure.addAttachment("Отправка запроса на действие с токеном, прошедшим аутентификацию и помещенным в хранилище",
                reporter.reportRequestResponse(request, Headers.getX_API_KEY(), "token=" + token + "&action="
                        + Actions.ACTION, response));

        step("Выход из учетной записи");
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGOUT);
        Allure.addAttachment("Выход из учетной записи", reporter.reportRequestResponse(request, Headers.getX_API_KEY(),
                "token=" + token + "&action=" + Actions.LOGOUT, response));

        step("Повторная отправка запроса на действие после выхода из учетной записи");
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGOUT);
        Allure.addAttachment("Повторная отправка запроса на действие после выхода из учетной записи",
                reporter.reportRequestResponse(request, Headers.getX_API_KEY(),"token=" + token + "&action="
                        + Actions.LOGOUT, response));

        validations.unavailableTokenInAction(response);


    }
}
