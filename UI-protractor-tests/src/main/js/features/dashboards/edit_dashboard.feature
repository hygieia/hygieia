#Feature: Edit Dashboard
#
#  As a software project stakeholder
#  I want to ensure that I am able edit a dashboard for my project
#  In order to make changes to the existing dashboards.
#
#  Scenario: User edits a team dashboard title
#    Given I am an authorized project stakeholder
#    And I am on the Hygieia home screen
#    When I edit a dashboard title to 'DummyTeamDashboardEdited'
#    Then the team dashboard title should be updated to 'DummyTeamDashboardEdited'
#
#  Scenario: User edits a product dashboard title
#    Given I am an authorized project stakeholder
#    And I am on the Hygieia home screen
#    When I edit a dashboard title to 'DummyProdDashboardEdited'
#    Then the team dashboard title should be updated to 'DummyProdDashboardEdited'
#
#  Scenario: User edits owner information of a dashboard
#    Given I am an authorized project stakeholder
#    And I am on the Hygieia home screen
#    When I edit the owner information of a dashboard
#    Then owner information should be updated for the dashboard