package com.ianwilliams.restfulapistesting.clients;

import io.restassured.RestAssured;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.useRelaxedHTTPSValidation;

@Slf4j
@Component
@AllArgsConstructor
public class CommonClient {

    public void getURI(String uri) {
        RestAssured.baseURI = uri;
        useRelaxedHTTPSValidation();
    }
}