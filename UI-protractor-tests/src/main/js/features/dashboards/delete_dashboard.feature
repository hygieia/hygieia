#Feature: Delete Dashboard
#
#  As a software project stakeholder
#  I want to ensure that I am able delete a dashboard for my project
#  In order to clean up unwanted dashboards.
#
#  @issues:TEART-2801
#  Scenario: User deletes a team dashboard
#    Given I am an authorized project stakeholder
#    And I am on the Hygieia home screen
#    When I delete a team dashboard
#    Then the dashboard 'DummyTeamDashboard' should be deleted
#
#  @issues:TEART-2801
#  Scenario: User deletes a product dashboard
#    Given I am an authorized project stakeholder
#    And I am on the Hygieia home screen
#    When I delete a team dashboard
#    Then the dashboard 'DummyProdDashboard' should be deleted