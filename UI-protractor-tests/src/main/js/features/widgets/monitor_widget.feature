#Feature: Monitor Widget
#
#  As a software project stakeholder
#  I want to ensure that I am able to monitor widget for my project
#  In order to view project metrics.
#
#  Scenario: User configures a monitor widget
#    Given I am an authorized project stakeholder
#    And I am on the Hygieia home screen
#    When I select a team dashboard named 'DummyTeamDashboard'
#    And I click on the settings button for monitor widget
#    And I click on Add Service Button
#    And I enter a service to be monitored
#    And I click on Add Service button for Dependent Services
#    And I enter a dependent service to be monitored
#    Then the monitor widget should display the right information
#
#  Scenario: User deletes a service for the monitor widget
#    Given I am an authorized project stakeholder
#    And I am on the Hygieia home screen
#    When I select a team dashboard named 'DummyTeamDashboard'
#    And I click on the settings button for monitor widget
#    And I click on Delete button for the displayed Service
#    Then the monitor widget should display the updated information
#
#  Scenario: User deletes a dependent service for the monitor widget
#    Given I am an authorized project stakeholder
#    And I am on the Hygieia home screen
#    When I select a team dashboard named 'DummyTeamDashboard'
#    And I click on the settings button for monitor widget
#    And I click on Delete button for the displayed Dependent Service
#    Then the monitor widget should display the updated information
#
#  Scenario: User edits the configuration of a monitor widget
#    Given I am an authorized project stakeholder
#    And I am on the Hygieia home screen
#    When I select a team dashboard named 'DummyTeamDashboard'
#    And I click on the settings button for monitor widget
#    And I change Dependent Service
#    And I click on Save button
#    Then the monitor widget should display the changes
#
#  Scenario: User cancels the configuration of a monitor widget
#    Given I am an authorized project stakeholder
#    And I am on the Hygieia home screen
#    When I select a team dashboard named 'DummyTeamDashboard'
#    And I click on the settings button for monitor widget
#    And I change Dependent Service
#    And I click on Cancel button
#    Then the monitor widget should display the same configuration
