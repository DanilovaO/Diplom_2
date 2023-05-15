package api;

import api.User;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import com.google.gson.Gson;

import static org.apache.http.HttpStatus.*;

import static io.restassured.RestAssured.given;

public class UserApi {

    public final String baseURI = "https://stellarburgers.nomoreparties.site";
    public final String userEndpoint = "/api/auth/register";
    public final String userLoginEndpoint = "/api/auth/login";
    public final String userDataEndpoint = "/api/auth/user";
    public final String orderDataEndpoint = "/api/orders";

    Gson gson = new Gson();

    @Step("Создаём пользователя ")
    public Response createUser(User user) {
        return given().header("Content-type", "application/json")
                .body(user)
                .post(userEndpoint);
    }

    @Step("Создаём пользователя ")
    public String createAndLoginUser(User user) {
        given().header("Content-type", "application/json")
                .body(user)
                .post(userEndpoint);
        Response loginResponse = given().header("Content-type", "application/json")
                .body(gson.toJson(user))
                .post(userLoginEndpoint);
        loginResponse.then().statusCode(SC_OK);
        return loginResponse.body().jsonPath().get("accessToken");
    }

    @Step("Удаление пользователя ")
    public Response deleteUser(String accessToken) {
        return given().header("Authorization", accessToken)
                .delete(userDataEndpoint);
    }

    @Step("Логин пользователя в системе")
    public Response loginUser(User user) {
        Response responseLogin = given().header("Content-type", "application/json")
                .body(user)
                .post(userLoginEndpoint);
        if (responseLogin.statusCode() != SC_OK) {
            return null;
        }
        return responseLogin;
    }
}
