@all @songs
Feature: Testing Spotify APIs

  Background:
    Given I have a valid access token

    # For all the scenarios i have not added the Given statement as this is already covered in Background.

  # Positive Scenarios
  Scenario: Retrieve a Track's Information
    And a valid track ID
    When I request the track information using the Spotify API
    Then I should receive a successful response with the track details

  Scenario: Search for a Playlist
    And a valid search query for a playlist
    When I search for the playlist using the Spotify API
    Then I should receive a successful response with a list of relevant playlists
    And each playlist in the response should match the search criteria

#  # Negative Scenarios
  Scenario: Retrieve Track Information with Invalid ID
    And an invalid track ID
    When I request the track information using the Spotify API
    Then I should receive an error response with an appropriate error message

  Scenario: Create a Playlist without Sufficient Permissions
    And a valid access token without the necessary permissions
    When I attempt to create a new playlist using the Spotify API
    Then I should receive an error response indicating insufficient permissions
