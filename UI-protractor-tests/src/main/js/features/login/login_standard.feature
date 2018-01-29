Feature: Standard Login

  As a software project stakeholder,
  I want to ensure that I am able to log in to Hygieia
  In order to interact with my dashboards.

  Scenario: User attempts to log in with invalid credentials
    Given I navigate to the login page
    When I select standard login page
    And I enter login credentials invalidUser and invalidPassword
    And I attempt to login
    Then I should be on the login page
    And I should see an error for wrong username or password

  Scenario: User attempts to log in with valid credentials
    Given I navigate to the login page
    When I select standard login page
    And I enter login credentials hygieia_test_user and password
    And I attempt to login
    Then I should be redirected to the home page
    And the welcome header should contain username hygieia_test_user