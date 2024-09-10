# CrudDataModel.feature
Feature: Aristotle Data Entity CRUD Operations

  Scenario: Create a New User
    Given there exists a User entity with nickName "Mark" and uidcid "6b23"
    Then we can query the user and retrieve the information

  Scenario: Update an Existing User
    Given there exists a User entity with nickName "Hook" and uidcid "6b47"
    When the Doctor entity is updated with the following changes:
      | uidcid         | Name              |
      | 6b44           | Mei               |
    Then we can query the user and retrieve the information

  Scenario Outline: API supports creating map in one step
    Given there exists a User entity with nickName "Gambit" and uidcid "vsr93"
    When when we create the graph with "<title>" and "<description>"
    Then we can query the graph and retrieve the information with "<title>" and "<description>"
    Examples:
      | title               | description            |
      | Journey to the West | written by Wu Cheng'en |
