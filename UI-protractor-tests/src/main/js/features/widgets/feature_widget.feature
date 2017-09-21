Feature: Feature Widget

  As a software project stakeholder
  I want to ensure that I am able to feature widget for my project
  In order to view project metrics.

  @issues:TEART-2802
  Scenario: User configures a feature widget
    Given I am an authorized project stakeholder
    And I am on the Hygieia home screen
    When I select a team dashboard named 'DummyTeamDashboard'
    And I click on the settings button for feature widget
    And I select Agile Content Tool Type
    And I enter a Project Name
    And I enter a Team Name
    And I select Estimate Metric
    And I select Sprint Type
    And I select List Feature Type
    And I click on Save button
    Then the feature widget should display the right information

  @issues:TEART-2802
  Scenario: Verify all the projects displayed for Project Name field
    Given I am an authorized project stakeholder
    And I am on the Hygieia home screen
    When I select a team dashboard named 'DummyTeamDashboard'
    And I click on the settings button for feature widget
    And I select Agile Content Tool Type
    And I click on the text box for Project Name
    Then all the available projects should be displayed

  @issues:TEART-2802
  Scenario: Verify the search functionality on the text box for Project Name
    Given I am an authorized project stakeholder
    And I am on the Hygieia home screen
    When I select a team dashboard named 'DummyTeamDashboard'
    And I click on the settings button for feature widget
    And I select Agile Content Tool Type
    And I click on the text box for Project Name
    And I type a search string for Project Name
    Then all the matching projects with the search string should be displayed

  @issues:TEART-2802
  Scenario: User edits the configuration of a feature widget
    Given I am an authorized project stakeholder
    And I am on the Hygieia home screen
    When I select a team dashboard named 'DummyTeamDashboard'
    And I click on the settings button for feature widget
    And I change List Feature Type
    And I click on Save button
    Then the feature widget should display the changes

  @issues:TEART-2802
  Scenario: User cancels the configuration of a feature widget
    Given I am an authorized project stakeholder
    And I am on the Hygieia home screen
    When I select a team dashboard named 'DummyTeamDashboard'
    And I click on the settings button for feature widget
    And I change List Feature Type
    And I click on Cancel button
    Then the feature widget should display the same configuration