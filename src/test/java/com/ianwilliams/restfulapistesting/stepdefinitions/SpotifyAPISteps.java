package com.ianwilliams.restfulapistesting.stepdefinitions;

import com.ianwilliams.restfulapistesting.model.SpotifyData;
import com.ianwilliams.restfulapistesting.model.Token;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.platform.commons.util.StringUtils.isNotBlank;

@Slf4j
public class SpotifyAPISteps {

    @Value("${iw.tokenUrl}")
    private String tokenUrl;

    @Value("${iw.clientId}")
    private String clientId;

    @Value("${iw.clientSecret}")
    private String clientSecret;

    @Value("${iw.userId}")
    private String userId;

    @Value("${iw.createPlaylist}")
    private String createPlaylist;

    @Value("${track.id}")
    private String trackId;

    private Response response;
    public Token token;
    private SpotifyData spotifyData;

    @Given("I have a valid access token")
    public void iHaveAValidAccessToken() {
        response = given()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .queryParam("grant_type", "client_credentials")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .when()
                .post(tokenUrl);

        assertThat(response.getStatusCode(), is(SC_OK));
        token = new Token(response.getBody().jsonPath().getString("access_token"));
    }

    @And("^(a valid|an invalid) track ID$")
    public void givenValidTrackID(String validInvalid) {
        if (!validInvalid.contains("invalid") && trackId.length() == 22 && isNotBlank(trackId)) {
            assertThat(trackId.length(), is(22));
            assertThat(isNotBlank(trackId), is(true));
        } else {
            trackId = "11dFghVXANMlKmJXsNCaNl";
        }
    }

    @When("I request the track information using the Spotify API")
    public void requestTrackInfo() {
        response = given()
                .header("Authorization", "Bearer " + token.getBearerToken())
                .when()
                .get("/tracks/" + trackId);
    }

    @Then("I should receive a successful response with the track details")
    public void validateTrackInfo() {
        JsonPath responseBody = response.getBody().jsonPath();
        assertThat(response.getStatusCode(), is(SC_OK));
        assertThat(responseBody.get("id"), equalTo(trackId));
        assertThat(responseBody.get("name"), equalTo("Cut To The Feeling"));
        assertThat(responseBody.get("artists"), notNullValue());
        assertThat(responseBody.get("album"), notNullValue());
    }

    @And("a valid search query for a playlist")
    public void aValidSearchQueryForAPlaylist() {
        spotifyData = SpotifyData.builder()
                .searchQuery("pitbull")
                .type("album")
                .market("ES")
                .limit(3).build();
    }

    @When("I search for the playlist using the Spotify API")
    public void iSearchForThePlaylistUsingTheSpotifyAPI() {
        response = given()
                .header("Authorization", "Bearer " + token.getBearerToken())
                .queryParam("q", spotifyData.getSearchQuery())
                .queryParam("type", spotifyData.getType())
                .queryParam("market", spotifyData.getMarket())
                .queryParam("limit", spotifyData.getLimit())
                .when()
                .get("/search");
    }

    @Then("I should receive a successful response with a list of relevant playlists")
    public void iShouldReceiveASuccessfulResponseWithAListOfRelevantPlaylists() {
        assertThat(response.getStatusCode(), is(SC_OK));
    }

    @And("each playlist in the response should match the search criteria")
    public void eachPlaylistInTheResponseShouldMatchTheSearchCriteria() {
        JsonPath responseBody = response.getBody().jsonPath();
        assertThat(responseBody.getString("albums.items.name"), containsString("Pitbull"));
        assertThat(responseBody.getString("albums.items.artists[0].name"), containsString("Pitbull"));
        assertThat(responseBody.getString("albums.items.type"), containsString("album"));
    }

    @Then("I should receive an error response with an appropriate error message")
    public void iShouldReceiveAnErrorResponseWithAnAppropriateErrorMessage() {
        assertThat(response.getStatusCode(), is(SC_NOT_FOUND));
        assertThat(response.getBody().jsonPath().get("error.message"), equalTo("Non existing id: 'spotify:track:11dFghVXANMlKmJXsNCaNl'"));
    }

    @Given("a valid access token without the necessary permissions")
    public void aValidAccessTokenWithoutTheNecessaryPermissions() {
        log.info("");
    }

    @When("I attempt to create a new playlist using the Spotify API")
    public void iAttemptToCreateANewPlaylistUsingTheSpotifyAPI() {
        String body = "{\n" +
                "    \"name\": \"Updated Playlist\",\n" +
                "    \"description\": \"New playlist description\",\n" +
                "    \"public\": true\n" +
                "}";
        response = given().log()
                .all(true)
                .header("Authorization", "Bearer " + token.getBearerToken())
                .contentType(ContentType.JSON)
                .pathParam("user_id", userId)
                .body(body)
                .when()
                .post(createPlaylist);
    }

    @Then("I should receive an error response indicating insufficient permissions")
    public void iShouldReceiveAnErrorResponseIndicatingInsufficientPermissions() {
        assertThat(response.getStatusCode(), is(SC_FORBIDDEN));
        assertThat(response.getBody().jsonPath().get("error.message"), equalTo("This request requires user authentication."));
    }
}