import api.User;
import api.UserApi;
import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;


public class LoginUserTest {
    private User user;
    private String accessToken;
    Gson gson = new Gson();
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
    @DisplayName("Проверка создания пользователя")
    public void checkCreateUser() {
        accessToken = userApi.createUser(user).body().jsonPath().get("accessToken");

        Response loginResponse = given().header("Content-type", "application/json")
                .body(gson.toJson(user))
                .post(userApi.userLoginEndpoint);
        loginResponse.then().statusCode(SC_OK);

        loginResponse.then().body("accessToken", not(emptyOrNullString()));

        JsonPath body = loginResponse.body().jsonPath();

        assert body.get("accessToken").toString().contains("Bearer");
        assert Objects.equals(body.get("success").toString(), "true");
        assert body.get("user").toString().equals(
                String.format("{email=%s, name=%s}", user.getEmail(), user.getName()).toLowerCase());
        assert !Objects.equals(body.get("refreshToken").toString(), "");    }

    @Test
    @DisplayName("Авторизация незарегестрированого пользователя")
    public void checkUserLoginWithIncorrectEmail() {

        user.setEmail("Incorrect" + user.getEmail());

        Response loginResponse = given().header("Content-type", "application/json")
                .body(gson.toJson(user))
                .post(userApi.userLoginEndpoint);
        loginResponse.then().statusCode(SC_UNAUTHORIZED);

        JsonPath body = loginResponse.body().jsonPath();
        assert Objects.equals(body.get("success").toString(), "false");
        assert Objects.equals(body.get("message").toString(), "email or password are incorrect");
    }


    @Test
    @DisplayName("Авторизация зарегестрированого с неправильным паролем")
    public void checkUserLoginWithIncorrectPasswordUserCreate() {
        accessToken = userApi.createUser(user).body().jsonPath().get("accessToken");

        user.setPassword("Incorrect" + user.getPassword());

        Response loginResponse = given().header("Content-type", "application/json")
                .body(gson.toJson(user))
                .post(userApi.userLoginEndpoint);
        loginResponse.then().statusCode(SC_UNAUTHORIZED);

        JsonPath body = loginResponse.body().jsonPath();

        assert Objects.equals(body.get("success").toString(), "false");
        assert Objects.equals(body.get("message").toString(), "email or password are incorrect");
    }

    @Test
    @DisplayName("Авторизация незарегестрированого с неправильным паролем")
    public void checkUserLoginWithIncorrectPasswordUserNotCreate() {
        accessToken = userApi.createUser(user).body().jsonPath().get("accessToken");

        user.setPassword("Incorrect" + user.getPassword());

        Response loginResponse = given().header("Content-type", "application/json")
                .body(gson.toJson(user))
                .post(userApi.userLoginEndpoint);
        loginResponse.then().statusCode(SC_UNAUTHORIZED);

        JsonPath body = loginResponse.body().jsonPath();

        assert Objects.equals(body.get("success").toString(), "false");
        assert Objects.equals(body.get("message").toString(), "email or password are incorrect");
    }
}

