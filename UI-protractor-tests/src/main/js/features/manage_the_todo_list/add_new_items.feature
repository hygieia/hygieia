Feature: Add new items to the todo list

  In order to avoid having to remember things that need doing
  As a forgetful person
  I want to be able to record what I need to do in a place where I won't forget about them

  @issues:JIRA-2,JIRA-3
  Scenario: Adding an item to an empty list
    Given that I have an empty todo list
    When I add Buy some milk to my list
    Then Buy some milk should be recorded in my list

  @issue:JIRA-4
  Scenario: Adding an item to a list with other items
    Given that I have a todo list containing Buy some cookies, Walk the dog
    When I add Buy some cereal to my list
    Then my todo list should contain Buy some cookies, Walk the dog, Buy some cereal