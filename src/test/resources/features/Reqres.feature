@all @rest
Feature: Testing ReqRes APIs

  Background:
    Given the website resreq.in is up

  # For all the scenarios i have not added the Given statement as this is already covered in Background.
  Scenario: Get List of Users
    When a GET request is made to retrieve the list of users
    Then the response status code should be 200
    And the response should contain a list of users

  Scenario: Get Single User
    When a GET request is made with a valid user ID
    Then the response status code should be 200
    And the response should contain the details of the user

  Scenario: Get Single User Not Found
    When a GET request is made with an invalid user ID
    Then the response status code should be 404
    And the response should contain an error message indicating user not found

  Scenario Outline: Create Resource
    When a POST request is made to create the resource with <name> <job>
    Then the response status code should be 201
    And the response should contain the <name> <job> of the newly created resource

    Examples:
      | name     | job             |
      | morpheus | leader          |
      | james    | associate       |
      | smith    | sales executive |

  Scenario: Update Resource with PUT
    When a PUT request is made with a valid resource ID
    Then the response status code should be 200
    And the response should contain the updated details of the resource

  Scenario: Delete Resource
    When a DELETE request is made with a valid resource ID
    Then the response status code should be 204

  Scenario Outline: Register User - Successful
    When a POST request is made to register a new user with <email> <password>
    Then the response status code should be 200
    And the response should contain the user's registration details

    Examples:
      | email              | password |
      | eve.holt@reqres.in | pistol   |

  Scenario: Register User - Unsuccessful
    When a POST request is made to register a new user with invalid data
    Then the response status code should be 400
    And the response should contain an error message indicating registration failure

  Scenario: Delayed Response
    When a GET request is made to the endpoint
    Then the response should be received after a certain delay