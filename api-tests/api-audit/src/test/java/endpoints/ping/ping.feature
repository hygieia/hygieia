Feature: ping

  Background:
    * url baseUrl

  Scenario: Verify ping api returns 'hello audit'
    Given path 'ping'
    When method get
    Then status 200
    And match response == '\"hello audit\"'