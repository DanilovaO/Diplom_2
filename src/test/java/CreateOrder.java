import api.OrderApi;
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
import java.util.ArrayList;
import java.util.Objects;


import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;

public class CreateOrder {

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
    @DisplayName("Создание пользовательского заказа")
    public void createUserOrder() {
        // Регистрация пользователя и получение токена
        String accessToken = userApi.createAndLoginUser(user);
        // Создание массива с ингредиентами
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa75");
        ingredients.add("61c0c5a71d1f82001bdaaa74");

        // Создание класса тела запроса
        OrderApi orderIngredients = new OrderApi(ingredients);

        //Запрос на создание заказа
        Response response =  given().header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(gson.toJson(orderIngredients))
                .post(userApi.orderDataEndpoint);
        response.then().statusCode(SC_OK);
        JsonPath body = response.body().jsonPath();

        assert Objects.equals(body.get("success").toString(), "true");
        assert Objects.equals(body.get("name").toString(), "Традиционный-галактический антарианский бургер");
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createUserOrderWithoutAuth() {
        // Создание массива с ингредиентами
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("61c0c5a71d1f82001bdaaa75");
        ingredients.add("61c0c5a71d1f82001bdaaa74");

        // Создание класса тела запроса
        OrderApi orderIngredients = new OrderApi(ingredients);

        //Запрос на создание заказа
        given().header("Content-type", "application/json")
                .body(gson.toJson(orderIngredients))
                .post(userApi.orderDataEndpoint).then().statusCode(SC_OK);

    }

    @Test
    @DisplayName("Создание заказа с невалидным ингедиентом")
    public void createUserOrderWithInvalidIngredients() {
        // Регистрация пользователя и получение токена
        String token = userApi.createAndLoginUser(user);
        // Создание массива с ингредиентами
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("");

        // Создание класса тела запроса
        OrderApi orderIngredients = new OrderApi(ingredients);

        //Запрос на создание заказа
        given().header("Content-type", "application/json")
                .header("Authorization", token)
                .body(gson.toJson(orderIngredients))
                .post(userApi.orderDataEndpoint)
                .then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createUserOrderWithEmptyIngredients() {
        // Регистрация пользователя и получение токена
        String token = userApi.createAndLoginUser(user);
        // Создание массива с ингредиентами
        ArrayList<String> ingredients = new ArrayList<>();

        // Создание класса тела запроса
        OrderApi orderIngredients = new OrderApi(ingredients);

        //Запрос на создание заказа
        Response response =  given().header("Content-type", "application/json")
                .header("Authorization", token)
                .body(gson.toJson(orderIngredients))
                .post(userApi.orderDataEndpoint);
        response.then().statusCode(SC_BAD_REQUEST);
        JsonPath body = response.body().jsonPath();

        assert Objects.equals(body.get("success").toString(), "false");
        assert Objects.equals(body.get("message").toString(), "Ingredient ids must be provided");

    }
}
