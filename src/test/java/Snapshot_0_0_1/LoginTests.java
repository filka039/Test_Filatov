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
@Epic("Login")
@DisplayName("Тестирование аутентификации")
public class LoginTests {

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
    @DisplayName("Успешная аутентификация")
    public void successLogin() {
        step("Аутентификация с валидным токеном и действием \"" + Actions.LOGIN + "\"");
        WireMockUtil.stubPost(WireMockConstants.LOGIN_ENDPOINT_MOCK, WireMockConstants.ACCESS_STATUS_MOCK,
                WireMockConstants.BODY_SUCCESS_MOCK);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация с валидным токеном и действием", reporter.reportRequestResponse(
                request, Headers.getX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN, response));

        validations.successLogin(response);
    }


    @Test
    @DisplayName("Аутентификация без заголовка X_API_KEY")
    public void withoutX_API_KEYLogin() {
        step("Аутентификация без заголовка X_API_KEY");
        response = request.postRequest("token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация без заголовка X_API_KEY",reporter.reportRequestResponse(request,
                "token=" + token + "&action=" + Actions.LOGIN, response));

        validations.wrongX_API_KEYOrToken(response);
    }


    @Test
    @DisplayName("Аутентификация с неверным заголовком X_API_KEY")
    public void wrongX_API_KEYLogin() {
        step("Аутентификация с неверным заголовком X_API_KEY");
        response = request.postRequest(Headers.getWrongX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация с неверным заголовком X_API_KEY",reporter.reportRequestResponse(request,
                Headers.getWrongX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN, response));

        validations.wrongX_API_KEYOrToken(response);
    }


    @Test
    @DisplayName("Аутентификация с пустым заголовком X_API_KEY")
    public void emptyX_API_KEYLogin() {
        step("Аутентификация с пустым заголовком X_API_KEY");
        response = request.postRequest(Headers.getEmptyX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация с пустым заголовком X_API_KEY",reporter.reportRequestResponse(request,
                Headers.getEmptyX_API_KEY(), "token=" + token + "&action=" + Actions.LOGIN, response));

        validations.wrongX_API_KEYOrToken(response);
    }


    @ParameterizedTest
    @DisplayName("Аутентификация c невалидным токеном")
    @MethodSource("invalidTokensProvider")
    public void invalidTokenLogin(String description, String methodToken) {
        step(description);
        response = request.postRequest(Headers.getEmptyX_API_KEY(), "token=" + methodToken + "&action=" + Actions.LOGIN);
        Allure.addAttachment("Аутентификация c невалидным токеном",reporter.reportRequestResponse(request,
                Headers.getX_API_KEY(), "token="+ methodToken + "&action=" + Actions.LOGIN, response));

        validations.wrongX_API_KEYOrToken(response);
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
    @DisplayName("Аутентификация с недопустимым параметром \"action\"")
    @MethodSource("invalidActionLogin")
    public void invalidActionLogin(String description, String methodAction) {
        step(description);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + methodAction);
        Allure.addAttachment("Аутентификация с недопустимым параметром \"action\"",reporter.reportRequestResponse(request,
                Headers.getX_API_KEY(), "token="+ token + "&action=" + methodAction, response));

        validations.invalidAction(response);
    }

    static Stream<Arguments> invalidActionLogin() {
        return Stream.of(
                Arguments.of("пустые действие", ""),
                Arguments.of("недопустимое действие", Actions.REGISTER));
    }


    @ParameterizedTest
    @DisplayName("Аутентификация c допустимым действием, отличным от \"LOGIN\"")
    @MethodSource("wrongActionLogin")
    public void wrongActionLogin(String description, String methodAction) {
        step(description);
        response = request.postRequest(Headers.getX_API_KEY(), "token=" + token + "&action=" + methodAction);
        Allure.addAttachment("Аутентификация c допустимым действием, отличным от \"LOGIN\"",reporter.reportRequestResponse(request,
                Headers.getX_API_KEY(), "token="+ token + "&action=" + methodAction, response));

        validations.wrongAction(response);
    }

    static Stream<Arguments> wrongActionLogin() {
        return Stream.of(
                Arguments.of("действие", Actions.ACTION),
                Arguments.of("действие", Actions.LOGOUT));
    }
}
