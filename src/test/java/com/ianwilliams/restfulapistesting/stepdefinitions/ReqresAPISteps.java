package com.ianwilliams.restfulapistesting.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.http.HttpStatus.SC_MULTIPLE_CHOICES;
import static org.apache.http.HttpStatus.SC_OK;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringContains.containsString;

@Slf4j
public class ReqresAPISteps {

    @Value("${iw.reqres.baseUrl}")
    private String reqresBaseUrl;

    @Value("${iw.users}")
    private String users;

    @Value("${iw.register}")
    private String register;

    private Response response;

    @Given("^the website resreq.in is up$")
    public void theEndpointForFetchingUsersIsUp() {
        try {
            URL url = new URL(reqresBaseUrl.replace("/api/", ""));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode >= SC_OK && responseCode < SC_MULTIPLE_CHOICES) {
                log.info("Endpoint is UP. Response code: " + responseCode);
            } else {
                log.info("Endpoint is DOWN. Response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            log.info("Endpoint is DOWN. Exception: " + e.getMessage());
        }
    }

    @When("a GET request is made to retrieve the list of users")
    public void aGETRequestIsMadeToRetrieveTheListOfUsers() {
        response = given()
                .contentType(ContentType.JSON)
                .queryParam("page", "2")
                .when()
                .get(users);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        assertThat(response.getStatusCode(), is(statusCode));
    }

    @And("the response should contain a list of users")
    public void theResponseShouldContainAListOfUsers() {
        JsonPath responseBody = response.getBody().jsonPath();
        assertThat(responseBody.get("page"), is(2));
        assertThat(responseBody.get("per_page"), is(6));

        List<Map<String, Object>> dataItems = responseBody.getList("data");
        dataItems.forEach(dataItem -> {
            Integer id = (Integer) dataItem.get("id");
            String email = (String) dataItem.get("email");
            String firstName = (String) dataItem.get("first_name");
            String lastName = (String) dataItem.get("last_name");

            assertThat(id, notNullValue());
            assertThat(email, notNullValue());
            assertThat(validateString(firstName), is(true));
            assertThat(validateString(lastName), is(true));
        });
    }

    @When("a GET request is made with a valid user ID")
    public void aGETRequestIsMadeWithAValidUserID() {
        response = given()
                .contentType(ContentType.JSON)
                .when()
                .get(users + "/2");
    }

    @And("the response should contain the details of the user")
    public void theResponseShouldContainTheDetailsOfTheUser() {
        JsonPath responseBody = response.getBody().jsonPath();
        assertThat(responseBody.get("data.id"), is(2));
        assertThat(responseBody.get("data.email"), is("janet.weaver@reqres.in"));
        assertThat(validateString(responseBody.get("data.first_name")), is(true));
        assertThat(validateString(responseBody.get("data.last_name")), is(true));
        assertThat(responseBody.get("data.avatar"), is("https://reqres.in/img/faces/2-image.jpg"));
    }

    @When("a GET request is made with an invalid user ID")
    public void aGETRequestIsMadeWithAnInvalidUserID() {
        response = given()
                .contentType(ContentType.JSON)
                .when()
                .get(users + "/23");
    }

    @And("the response should contain an error message indicating user not found")
    public void theResponseShouldContainAnErrorMessageIndicatingUserNotFound() {
        assertThat(response.getBody().prettyPrint(), is("{\n    \n}"));
    }

    @When("^a POST request is made to create the resource with (.*) (.*)$")
    public void aPOSTRequestIsMadeToCreateTheResource(String name, String job) {
        String body = "{\n" +
                "    \"name\": \"" + name + "\",\n" +
                "    \"job\":  \"" + job + "\" \n" +
                "}";

        response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(users);
    }

    @And("^the response should contain the (.*) (.*) of the newly created resource$")
    public void theResponseShouldContainTheDetailsOfTheNewlyCreatedResource(String name, String job) {
        JsonPath responseBody = response.getBody().jsonPath();
        assertThat(responseBody.get("name"), is(name));
        assertThat(responseBody.get("job"), is(job));
    }

    @When("a PUT request is made with a valid resource ID")
    public void aPUTRequestIsMadeWithAValidResourceID() {
        String body = "{\n" +
                "    \"name\": \"smith\",\n" +
                "    \"job\":  \"zion resident\" \n" +
                "}";

        response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put(users + "/2");
    }

    @And("the response should contain the updated details of the resource")
    public void theResponseShouldContainTheUpdatedDetailsOfTheResource() {
        JsonPath responseBody = response.getBody().jsonPath();
        assertThat(responseBody.get("name"), is("smith"));
        assertThat(responseBody.get("job"), is("zion resident"));
    }

    @When("a DELETE request is made with a valid resource ID")
    public void aDELETERequestIsMadeWithAValidResourceID() {
        response = given()
                .contentType(ContentType.JSON)
                .when()
                .delete(users + "/2");
    }

    @When("^a POST request is made to register a new user with (.*) (.*)$")
    public void aPOSTRequestIsMadeToRegisterANewUser(String email, String password) {
        String body;
        if (email.contains("invalid")) {
            body = "{\n" +
                    "    \"email\":  \"" + password + "\" \n" +
                    "}";
        } else {
            body = "{\n" +
                    "    \"email\": \"" + email + "\",\n" +
                    "    \"password\":  \"" + password + "\" \n" +
                    "}";
        }

        response = given().log().all(true)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(register);
    }

    @And("the response should contain the user's registration details")
    public void theResponseShouldContainTheUserSRegistrationDetails() {
        JsonPath responseBody = response.getBody().jsonPath();
        assertThat(responseBody.get("id"), is(4));
        assertThat(responseBody.get("token"), is("QpwL5tke4Pnpja7X41"));
    }

    @And("the response should contain an error message indicating registration failure")
    public void theResponseShouldContainAnErrorMessageIndicatingRegistrationFailure() {
        assertThat(response.getBody().jsonPath().get("error"), containsString("Missing password"));
    }

    @When("a GET request is made to the endpoint")
    public void aGETRequestIsMadeToTheEndpoint() {
        await().atMost(1, MINUTES)
                .pollInterval(1, SECONDS)
                .pollDelay(1, SECONDS)
                .until(() -> getDelayedResponse()
                        .getStatusCode(), is(SC_OK));

        response = getDelayedResponse();
    }

    @Then("the response should be received after a certain delay")
    public void theResponseShouldBeReceivedAfterACertainDelay() {
        JsonPath responseBody = response.getBody().jsonPath();
        assertThat(responseBody.get("page"), is(1));
        assertThat(responseBody.get("per_page"), is(6));
        assertThat(responseBody.get("total"), is(12));
        assertThat(responseBody.get("total_pages"), is(2));
    }

    private static boolean validateString(String string) {
        return Pattern.compile("^[a-zA-Z]+$").matcher(string).matches();
    }

    private Response getDelayedResponse() {
        return given()
                .contentType(ContentType.JSON)
                .queryParam("delay", "3")
                .when()
                .get(users);
    }
}