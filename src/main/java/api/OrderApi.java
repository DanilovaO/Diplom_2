package api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;

public class OrderApi {

    public final String orderDataEndpoint = "/api/orders";

    Gson gson = new Gson();

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    private List<String> ingredients;

    public OrderApi(List<String> ingredients){
        this.ingredients = ingredients;
    }

    public OrderApi() {

    }

    @Step("Создаём пользователя ")
    public Boolean createOrder(String userToken, ArrayList<String> ingredients) {
        // Создание класса тела запроса
        OrderApi orderIngredients = new OrderApi(ingredients);

        //Запрос на создание заказа
        Response response =  given().header("Content-type", "application/json")
                .header("Authorization", userToken)
                .body(gson.toJson(orderIngredients))
                .post(orderDataEndpoint);
        response.then().statusCode(SC_OK);

        return response.body().jsonPath().get("success");
    }
}
