#Feature: LDAP Login
#
#  As a capital one software project stakeholder,
#  I want to ensure that I am able to log in to Hygieia with LDAP credentials
#  In order to interact with my dashboards.
#
#  Scenario: User attempts to login with invalid LDAP credentials
#    Given I navigate to the login page
#    When I select ldap login page
#    And I enter invalid credentials
#    And I attempt to log in
#    Then I should be on the login page
#    And I should see an error for wrong username or password
#
#  Scenario: User attempts to login with valid LDAP credentials
#    Given I navigate to the login page
#    When I select ldap login page
#    And I enter valid credentials
#    And I attempt to log in
#    Then I should be redirected to the home page
#    And the welcome header should contain my name