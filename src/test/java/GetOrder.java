import api.OrderApi;
import api.User;
import api.UserApi;
import api.response.OrderResponse;
import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

public class GetOrder {

    private User user;
    private UserApi userApi = new UserApi();
    private OrderApi orderApi = new OrderApi();
    private String accessToken;


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
    @DisplayName("Получение заказа с авторизацией")
    public void getUserOrder() {
        // Регистрация пользователя и получение токена
        String token = userApi.createAndLoginUser(user);
        // Создание заказа

        // Создание массива с ингредиентами
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa75");
        ingredients.add("61c0c5a71d1f82001bdaaa74");

        Boolean createOrderStatus = orderApi.createOrder(token, ingredients);
        assert Objects.equals(createOrderStatus, true);

        //Запрос на создание заказа
        Response response = given().header("Content-type", "application/json")
                .header("Authorization", token)
                .get(userApi.orderDataEndpoint);
        response.then().statusCode(SC_OK);

        OrderResponse body = response.getBody().as(OrderResponse.class);

        assert Objects.equals(body.success, true);
        assert Objects.equals(body.orders.size(), 1);
        assert Objects.equals(body.orders.get(0).getIngredients(), ingredients);
    }


    @Test
    @DisplayName("Получение заказов пользователя")
    public void getUserOrders() {
        // Регистрация пользователя и получение токена
        accessToken = userApi.createAndLoginUser(user);

        // Создание заказа

        // Создание массива с ингредиентами
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa75");
        ingredients.add("61c0c5a71d1f82001bdaaa74");

        Boolean createOrderStatus = orderApi.createOrder(accessToken, ingredients);
        assert Objects.equals(createOrderStatus, true);


        createOrderStatus = orderApi.createOrder(accessToken, ingredients);
        assert Objects.equals(createOrderStatus, true);

        //Запрос на создание заказа
        Response response = given().header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .get(userApi.orderDataEndpoint);
        response.then().statusCode(SC_OK);

        OrderResponse body = response.getBody().as(OrderResponse.class);

        assert Objects.equals(body.success, true);
        assert Objects.equals(body.orders.size(), 2);
        assert Objects.equals(body.orders.get(0).getIngredients(), ingredients);
        assert Objects.equals(body.orders.get(1).getIngredients(), ingredients);
    }

    @Test
    @DisplayName("Получение заказов с авторизацией")
    public void getUserOrderUnAuthUser() {
        //Запрос на создание заказа
        Response response = given().header("Content-type", "application/json")
                .get(userApi.orderDataEndpoint);

        response.then().statusCode(SC_UNAUTHORIZED);
        JsonPath body = response.body().jsonPath();

        assert Objects.equals(body.get("success"), false);
        assert Objects.equals(body.get("message").toString(), "You should be authorised");
    }
}