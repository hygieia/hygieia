#Feature: Feature Widget
#
#  As a software project stakeholder
#  I want to ensure that I am able to feature widget for my project
#  In order to view project metrics.
#
#  Scenario: User configures a feature widget
#    Given I login with valid credentials hygieia_test_user and password
#    And I should be redirected to the home page
#    When I create a dashboard with "Team dashboard" "Select Templates" "Cap One" "FeatureWidgetDashboard" "FeatureWidgetApp"
#    And the current dashboard header should read FeatureWidgetDashboard
#    And I click on settings button for feature widget
#    And I select Agile Content Tool Type "Jira"
#    And I enter a Project Name "Shared Tools ART"
#    And I enter a Team Name "Hygieia"
#    And I select Estimate Metric "Story Points"
#    And I select Sprint Type "Kanban"
#    And I select List Feature Type "Epics"
#    And I click on Save button
#    Then the feature widget should display the Project Name "Shared Tools ART"
#    And I navigate to home page
#    And I should be redirected to the home page
#    And I click on delete button for FeatureWidgetDashboard
#    And I confirm delete
##
##  Scenario: Verify all the projects displayed for Project Name field
##    Given I am an authorized project stakeholder
##    And I am on the Hygieia home screen
##    When I select a team dashboard named 'DummyTeamDashboard'
##    And I click on the settings button for feature widget
##    And I select Agile Content Tool Type
##    And I click on the text box for Project Name
##    Then all the available projects should be displayed
##
##  Scenario: Verify the search functionality on the text box for Project Name
##    Given I am an authorized project stakeholder
##    And I am on the Hygieia home screen
##    When I select a team dashboard named 'DummyTeamDashboard'
##    And I click on the settings button for feature widget
##    And I select Agile Content Tool Type
##    And I click on the text box for Project Name
##    And I type a search string for Project Name
##    Then all the matching projects with the search string should be displayed
##
##  Scenario: User edits the configuration of a feature widget
##    Given I am an authorized project stakeholder
##    And I am on the Hygieia home screen
##    When I select a team dashboard named 'DummyTeamDashboard'
##    And I click on the settings button for feature widget
##    And I change List Feature Type
##    And I click on Save button
##    Then the feature widget should display the changes
##
##  Scenario: User cancels the configuration of a feature widget
##    Given I am an authorized project stakeholder
##    And I am on the Hygieia home screen
##    When I select a team dashboard named 'DummyTeamDashboard'
##    And I click on the settings button for feature widget
##    And I change List Feature Type
##    And I click on Cancel button
##    Then the feature widget should display the same configuration