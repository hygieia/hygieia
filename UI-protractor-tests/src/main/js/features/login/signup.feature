#Feature: Hygieia Sign Up
#
#  As a software project stakeholder,
#  I want to ensure that I am able to Sign Up to Hygieia
#  In order to interact with my dashboards.
#
#  Scenario: User attempts to sign up with invalid credentials
#    Given I navigate to the signup page
#    When I enter invalid credentials
#    And I attempt to signup
#    Then I should be on the signup page
#    And I should see an error for invalid username or password
#
#  Scenario: User attempts to sign up with valid credentials
#    Given I navigate to the signup page
#    When I enter invalid credentials
#    And I attempt to signup
#    Then I should be redirected to the home page
#    And the welcome header should contain my name