# Task: Test a RESTful API using Spring Boot and Cucumber

The purpose of this repository is to test the Restful API's available in publicly.
It involves performing CRUD actions and asserting the reponse.

### Generate New Bearer Token

To manually verify any spotify endpoints using postman please generate the bearer token by following the below method.

Generate the new bearertoken (if the existing token is not working/expired) please use the below curl:

```
curl -X POST "https://accounts.spotify.com/api/token" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "grant_type=client_credentials&client_id=da2bd8fbd90642d9b321d0b0e38473c0&client_secret=17141ea9f7254524b8d4f1f5514cefe6"
```

### Technologies

- Java
- Springboot
- Gradle
- Cucumber
- Lombok
- Rest Assured
- Hamcrest Library
- Awaitility Library

### Public Endpoints

To test the RESTful API's [Spotify](https://developer.spotify.com/documentation/web-api) & [reqres](https://reqres.in/)
endpoints are used in this repo.

### Test scenarios

The cucumber scenarios include the following conditions:

- Positive scenarios
- Negative scenarios
- Authentication scenarios
- Data validation scenarios
- Assertion scenarios

***I aimed to deliberately <font color="red">fail</font> the scenario titled `Register User - Successful` to show it in
report.***

### Running the tests

To run the tests use any one approach from the following :

- Right click on cucumber scenario and choose run scenario option from the context menu.
  OR
- Right click TestRunner and choose run scenario option from the context menu.
  OR
- Using terminal, navigate to this repo and enter the command `./gradlew test`

### Report

To view the cucumber report, please navigate to `RestfulAPIsTesting/build/reports/tests/` and open the `cucumber.html`
in your favourite browser.

### Areas of improvement

- There is a possibility to refactor a few methods.
- We can create clients such as GetClient, PostClient, and DeleteClient etc to prevent redundant code in step definition methods.
- Consider implementing better reporting mechanisms like extent reports to enhance the clarity of results.
- Integration with Jenkins is required, so a Jenkins file should be added to enable execution in Jenkins.
- For POST requests, it's advisable to store the body payload in JSON files located in the resources directory.