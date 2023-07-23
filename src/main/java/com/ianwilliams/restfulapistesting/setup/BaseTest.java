package com.ianwilliams.restfulapistesting.setup;

import com.ianwilliams.restfulapistesting.clients.CommonClient;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class BaseTest {

    @Autowired
    private CommonClient commonClient;

    @Value("${iw.spotify.baseUrl}")
    private String spotifyBaseUrl;

    @Value("${iw.reqres.baseUrl}")
    private String reqresBaseUrl;


    @Before
    public void setUp(Scenario scenario) {
        if (scenario.getSourceTagNames().contains("@songs")) {
            commonClient.getURI(spotifyBaseUrl);
        } else {
            commonClient.getURI(reqresBaseUrl);
        }
    }
}