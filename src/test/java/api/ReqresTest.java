package api;

import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;


import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ReqresTest {
    private final static String URL = "https://reqres.in/";
    private final static List<String> list = new ArrayList<>();

    @Test
    public void getAllUsers() {
        Specification.installSpecification(Specification.reqSpec(URL), Specification.respSpec200OK());
        List<UserData> allUsers = given()
                .when()
                .get("api/users")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
    }

    @Test
    public void getAllAvatar() {
        Specification.installSpecification(Specification.reqSpec(URL), Specification.respSpec200OK());
        List<UserData> allUsers = given()
                .when()
                .get("api/users")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        list.addAll(allUsers.stream().map(UserData::getAvatar).toList());
        System.out.println(list);
    }

    @Test
    public void checkIdUsers() {
        Specification.installSpecification(Specification.reqSpec(URL), Specification.respSpec200OK());
        List<UserData> allUsers = given()
                .when()
                .get("api/users")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        allUsers.forEach(u -> Assert.assertTrue(u.getAvatar().contains(u.getId().toString())));

    }

    @Test
    public void emailEndWith() {
        Specification.installSpecification(Specification.reqSpec(URL), Specification.respSpec200OK());
        List<UserData> allUsers = given()
                .when()
                .get( "api/users")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        Assert.assertTrue(allUsers.stream().allMatch(u -> u.getEmail().endsWith("@reqres.in")));
    }

    @Test
    public void avatarContainsId() {
        Specification.installSpecification(Specification.reqSpec(URL), Specification.respSpec200OK());
        List<UserData> allUsers = given()
                .when()
                .get("api/users")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);

        List<String> avatars = allUsers.stream().map(UserData::getAvatar).toList();
        List<String> ids = allUsers.stream().map(z -> z.getId().toString()).toList();

        for (int i = 0; i < avatars.size(); i++) {
            Assert.assertTrue(avatars.get(i).contains(ids.get(i)));
        }
    }
}
