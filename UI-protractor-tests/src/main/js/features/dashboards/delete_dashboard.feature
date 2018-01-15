Feature: Delete Dashboard

  As a software project stakeholder
  I want to ensure that I am able delete a dashboard for my project
  In order to clean up unwanted dashboards.

  Scenario: User deletes a team dashboard
    Given I navigate to home page
    And I should be redirected to the home page
    When I click on delete button for DummyTeamDashboard
    And I confirm delete
    Then the dashboard DummyTeamDashboard should be deleted
    And click on logout

#  Scenario: User deletes a product dashboard
#    Given I am an authorized project stakeholder
#    And I am on the Hygieia home screen
#    When I delete a team dashboard
#    Then the dashboard 'DummyProdDashboard' should be deleted