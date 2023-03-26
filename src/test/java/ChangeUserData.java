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


public class ChangeUserData {
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
    @DisplayName("Получение пользовательских данных")
    public void checkUserData() {
        String token = userApi.createAndLoginUser(user);

        Response getDataResponse = given().header("Content-type", "application/json")
                .body(gson.toJson(user)).header("authorization", token)
                .get(userApi.userDataEndpoint);
        getDataResponse.then().statusCode(SC_OK);
        JsonPath body = getDataResponse.body().jsonPath();
        assert Objects.equals(body.get("success").toString(), "true");
        assert body.get("user").toString().equals(
                String.format("{email=%s, name=%s}", user.getEmail(), user.getName()).toLowerCase());
    }

    @Test
    @DisplayName("Изменение пользовательских данных")
    public void changeUserData() {
        String token = userApi.createAndLoginUser(user);

        Response getDataResponse = given().header("Content-type", "application/json")
                .body(gson.toJson(user)).header("authorization", token)
                .get(userApi.userDataEndpoint);
        getDataResponse.then().statusCode(SC_OK);
        JsonPath body = getDataResponse.body().jsonPath();
        assert Objects.equals(body.get("success").toString(), "true");
        assert body.get("user").toString().equals(
                String.format("{email=%s, name=%s}", user.getEmail(), user.getName()).toLowerCase());

        user.setEmail("new_" + user.getEmail());

        Response changeDataResponse = given().header("Content-type", "application/json")
                .body(gson.toJson(user)).header("authorization", token)
                .patch(userApi.userDataEndpoint);
        changeDataResponse.then().statusCode(SC_OK);
        JsonPath newBody = changeDataResponse.body().jsonPath();
        assert Objects.equals(newBody.get("success").toString(), "true");
        assert newBody.get("user").toString().equals(
                String.format("{email=%s, name=%s}", user.getEmail(), user.getName()).toLowerCase());
    }

    @Test
    @DisplayName("Изменение email на уже использующийся")
    public void changeUserEmailOnAlreadyExists() {
        String token = userApi.createAndLoginUser(user);
        User newUser = new User(user);
        newUser.setEmail("new_" + user.getEmail());
        userApi.createUser(newUser);

        Response getDataResponse = given().header("Content-type", "application/json")
                .body(gson.toJson(user)).header("authorization", token)
                .get(userApi.userDataEndpoint);
        getDataResponse.then().statusCode(SC_OK);
        JsonPath body = getDataResponse.body().jsonPath();
        assert Objects.equals(body.get("success").toString(), "true");
        assert body.get("user").toString().equals(
                String.format("{email=%s, name=%s}", user.getEmail(), user.getName()).toLowerCase());

        Response changeDataResponse = given().header("Content-type", "application/json")
                .body(gson.toJson(newUser)).header("authorization", token)
                .patch(userApi.userDataEndpoint);
        changeDataResponse.then().statusCode(SC_FORBIDDEN);
        JsonPath newBody = changeDataResponse.body().jsonPath();
        assert Objects.equals(newBody.get("success").toString(), "false");
        assert Objects.equals(newBody.get("message").toString(), "User with such email already exists");
    }

    @Test
    @DisplayName("Изменение пользовательских данных у неавторизированого пользователя")
    public void changeUnAuthUserData() {
        userApi.createUser(user);

        Response changeDataResponse = given().header("Content-type", "application/json")
                .body(gson.toJson(user))
                .patch(userApi.userDataEndpoint);
        changeDataResponse.then().statusCode(SC_UNAUTHORIZED);
        JsonPath body = changeDataResponse.body().jsonPath();
        assert Objects.equals(body.get("success").toString(), "false");
        assert Objects.equals(body.get("message").toString(), "You should be authorised");
    }
}

