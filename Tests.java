package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

public class Tests {
    private final static String URL = "https://reqres.in/";


    @Test
    public void testGet_1() {

        int expectedId = 9;
        Response response = RestAssured.given()
                .when()                     // pre-conditions
                .contentType(ContentType.JSON)
                .get(URL + "api/users?page=2")  //указываем какой метод апи вызываем
                .then().log().all()
                .extract().response();

        int responseStatusCode = response.getStatusCode();

        Integer responseId = response.path("data[2].id");  // с помощью jsonPath вытягиваем айди 3го клиента

        Assert.assertTrue("WRONG ID. Actual=" + responseId + "; Expected=" + expectedId, expectedId == responseId);
        Assert.assertEquals(responseStatusCode, 200);

    }

    @Test
    public void testGet_2(){

        // при помощи метода RA - проверяем что вернется код 404 если по айди не найден клиент

        RestAssured.given()
                .when()
                .contentType(ContentType.JSON)
                .get(URL + "api/users/23")
                .then().log().all()
                .statusCode(404)
                .extract().response();

    }


    @Test
    public void testPostCreate (){

        //создание клиента

        String name ="Tester";
        String job = "jobester";
        String requestBody = "{\n" +
                "\t\"name\":\"" + name + "\",\n" +
                "\t\"job\":\"" + job + "\"" +
                 "\n}";

        Response response = RestAssured.given()
                .when()
                .contentType(ContentType.JSON)
                .body(requestBody)   // в боди передаем name, job
                .post(URL + "api/users")
                .then().log().all()
                .statusCode(201)
                .extract().response();

        String actualName = response.path("name");
        String actualJob = response.path("job");
        Integer actualId =  Integer.valueOf(response.path("id").toString()) ; // переводим айди в число, т.к. метод notSame не сравнивает строки
        String createdAt = response.path("createdAt");

        Assert.assertNotNull(createdAt);
        Assert.assertNotSame(0, actualId  );
        Assert.assertEquals(name,actualName);
        Assert.assertEquals(job,actualJob);
    }

    @Test
    public void testDelete(){

        //проверка что вернет 204 код при удалении

        RestAssured.given()
                .when()
                .contentType(ContentType.JSON)
                .delete(URL + "api/users/2")
                .then().log().all()
                .statusCode(204);

    }

}



