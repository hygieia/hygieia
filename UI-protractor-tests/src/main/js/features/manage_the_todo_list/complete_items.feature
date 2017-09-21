Feature: Complete items

  In order to feel a sense of accomplishment
  As an active doer
  I want to be able to mark the items I've completed

  Scenario: Completing an item
    Given that I have a todo list containing Write an article
    When I complete Write an article
    Then Write an article should be marked as completed