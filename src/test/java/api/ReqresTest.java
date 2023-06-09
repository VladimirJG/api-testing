package api;

import org.testng.Assert;
import org.testng.annotations.Test;


import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
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
                .get("api/users")
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

    @Test
    public void successRegTest() {
        Specification.installSpecification(Specification.reqSpec(URL), Specification.respSpec200OK());
        Integer id = 4;
        String token = "QpwL5tke4Pnpja7X4";
        Register user = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);
        Assert.assertNotNull(successReg.getId());
        Assert.assertNotNull(successReg.getToken());

        Assert.assertEquals(id, successReg.getId());
        Assert.assertEquals(token, successReg.getToken());
    }

    @Test
    public void unSuccessRegTest() {
        Specification.installSpecification(Specification.reqSpec(URL), Specification.respSpecError400());
        Register user = new Register("sydney@fife", "");
        UnSuccessReg unSuccessReg = given()
                .body(user)
                .post("api/register")
                .then().log().all()
                .extract().as(UnSuccessReg.class);
        Assert.assertEquals(unSuccessReg.getError(), "Missing password");
    }

    @Test
    public void sortedYearsTest() {
        Specification.installSpecification(Specification.reqSpec(URL), Specification.respSpec200OK());
        List<Pantone> pantoneList = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", Pantone.class);
        List<Integer> years = new ArrayList<>(pantoneList.stream().map(Pantone::getYear).sorted().toList());
        Assert.assertEquals(years, years.stream().sorted().toList());
    }

    @Test
    public void delete() {
        Specification.installSpecification(Specification.reqSpec(URL), Specification.respSpecUnique(204));
        given().when().delete("api/users/2")
                .then().log().all();
    }

    @Test
    public void compareDate() {
        Specification.installSpecification(Specification.reqSpec(URL), Specification.respSpec200OK());
        UserTime userTime = new UserTime("morpheus", "zion resident");
        UserTimeResponse response = given().body(userTime).when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UserTimeResponse.class);
        String regex = "\\..*$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex,"");
        Assert.assertEquals(currentTime, response.getUpdatedAt().replaceAll(regex,""));
    }
}
