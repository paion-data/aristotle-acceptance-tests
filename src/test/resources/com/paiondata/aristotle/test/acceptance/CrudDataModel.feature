# CrudDataModel.feature
Feature: Aristotle Data Entity CRUD Operations

  Scenario: API supports creating a new User
    Given there exists a User entity with nickName "Mark" and uidcid "6b23"
    Then we can query the user and retrieve the information

  Scenario: API supports updating an existing User
    Given there exists a User entity with nickName "Hook" and uidcid "6b47"
    When the User entity is updated with the following changes:
      | uidcid         | nickName          |
      | 6b47           | Mei               |
    Then we can query the user and retrieve the information

  Scenario Outline: API supports creating map in one step
    Given there exists a User entity with nickName "Gambit" and uidcid "vsr93"
    When when we create the graph with "<title>" and "<description>"
    Then we can query the graph and retrieve the information with "<title>" and "<description>"
    Examples:
      | title               | description            |
      | Journey to the West | written by Wu Cheng'en |

  Scenario Outline: API supports updating an existing graph
    Given we create a User with "<nickName>" and "<uidcid>" and add a graph with "<title>" and "<description>"
    When we can query the graph and retrieve the information with "<title>" and "<description>"
    And we update the graph with "<newTitle>" and "<newDescription>"
    Then we can query the graph and retrieve the information with "<newTitle>" and "<newDescription>"
    Examples:
      |nickName |uidcid | title               | description            |newTitle            |newDescription |
      |Alpha    |vsr97  | Journey to the West | written by Wu Cheng'en |Journey to the East |Dan'e          |
