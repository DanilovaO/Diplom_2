import api.User;
import api.UserApi;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.apache.http.HttpStatus.*;


public class CreateUserTest {
    private User user;
    private String accessToken;

    private UserApi userApi = new UserApi();

    @Before
    public void setUp() {
        RestAssured.baseURI = userApi.baseURI;
        String login = RandomStringUtils.randomAlphanumeric(7);
        user = new User(login + "@qqq1234567.ru", "autotest123", "autotest111");
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            userApi.deleteUser(accessToken).then().statusCode(SC_ACCEPTED);
        }

    }

    @Test
    @DisplayName("Проверяем статус-код и тело ответа при успешном создании")
    public void checkStatusCodeSuccessCreate() {
        Response response = userApi.createUser(user);
        accessToken = response.jsonPath().getString("accessToken");

        response.then()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Проверяем статус-код и тело ответа при попытке создать уже существующий аккаунт пользователя")
    public void checkDuplicateUserCreateStatus() {
        Response resp = userApi.createUser(user);
        resp.then()
                .statusCode(SC_OK)
                .and()
                .assertThat()
                .body("success", equalTo(true));
        accessToken = resp.body().jsonPath().get("accessToken");
        userApi.createUser(user).then().statusCode(SC_FORBIDDEN).and()
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Проверяем создание пользователя без заполненного обязательного поля: email")
    public void checkCreateUserWithEmptyEmail() {
        user.setEmail(null);
        userApi.createUser(user).then().statusCode(SC_FORBIDDEN)
                .and()
                .assertThat()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
