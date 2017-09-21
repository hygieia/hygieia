Feature: Filter the list to find items of interest

  In order to focus on things that matter
  James would like to filter his todo list
  to only show items of interest

  Scenario Outline: Viewing <applied filter> items only

    Given that I have a todo list containing Write some code, Walk the dog
    And I complete Write some code
    When I filter my list to show only <applied filter> tasks
    Then my todo list should contain <expected result>

    Examples:
      | applied filter | expected result |
      | Active         | Walk the dog    |
      | Completed      | Write some code |

  Scenario: Removing the filters to view all the items

    Given that I have a todo list containing Write some code, Walk the dog
    And I complete Write some code
    When I filter my list to show only Active tasks
    And I filter my list to show All tasks
    Then my todo list should contain Write some code, Walk the dog