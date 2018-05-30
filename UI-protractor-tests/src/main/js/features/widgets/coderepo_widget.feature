#Feature: CodeRepo Widget
#
#  As a software project stakeholder
#  I want to ensure that I am able to build widget for my project
#  In order to view project metrics.
#
#  Scenario: User configures a coderepo widget
#    Given I login with valid credentials hygieia_test_user and password
#    And I should be redirected to the home page
#    When I create a dashboard with "Team dashboard" "Select Templates" "Cap One" "CodeRepoWidgetDashboard" "CodeRepoWidgetApp"
#    And the current dashboard header should read CodeRepoWidgetDashboard
#    And I click on settings button for coderepo widget
#    And I set a repo type "GitHub"
#    And I set a repo url "https://github.com/capitalone/Hygieia"
#    And I set a branch "master"
#    And I click on Save button
#    Then the coderepo widget should display the issues label "ISSUES, PULLS AND COMMITS PER DAY"
#    And I navigate to home page
#    And I should be redirected to the home page
#    And I click on delete button for CodeRepoWidgetDashboard
#    And I confirm delete
#
##
##  Scenario: Verify the search functionality on the text box for Build Job
##    Given I am an authorized project stakeholder
##    And I am on the Hygieia home screen
##    When I select a team dashboard named 'DummyTeamDashboard'
##    And I click on the settings button for feature widget
##    And I type a search string for Build Job
##    Then all the matching build jobs with the search string should be displayed
##
##  Scenario: User edits the configuration of a build widget
##    Given I am an authorized project stakeholder
##    And I am on the Hygieia home screen
##    When I select a team dashboard named 'DummyTeamDashboard'
##    And I click on the settings button for build widget
##    And I change Build Job
##    And I click on Save button
##    Then the feature widget should display the changes
##
##  Scenario: User cancels the configuration of a build widget
##    Given I am an authorized project stakeholder
##    And I am on the Hygieia home screen
##    When I select a team dashboard named 'DummyTeamDashboard'
##    And I click on the settings button for build widget
##    And I change Buid Job
##    And I click on Cancel button
##    Then the feature widget should display the same configuration
##
##  Scenario: Verify the build widget turns red when there are more than 5 consecutive build failures
##    Given I am an authorized project stakeholder
##    And I am on the Hygieia home screen
##    When I select a team dashboard named 'DummyTeamDashboard'
##    And I click on the settings button for feature widget
##    And I enter a build job which failed for the last 5 times
##    And I click on Save button
##    Then build widget should be displayed in red color