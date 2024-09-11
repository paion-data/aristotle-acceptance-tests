# CrudDataModel.feature
Feature: Aristotle Data Entity CRUD Operations

  Scenario: API supports creating a new User
    Given create a User entity with nickName "Mark" and uidcid "6b23"
    Then we can query the user and retrieve the information

  Scenario: API supports updating an existing User
    Given create a User entity with nickName "Hook" and uidcid "6b47"
    When the User entity is updated with the following changes:
      | uidcid         | nickName          |
      | 6b47           | Mei               |
    Then we can query the user and retrieve the information

  Scenario Outline: API supports creating map in one step
    Given create a User entity with nickName "Gambit" and uidcid "vsr93"
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

  Scenario Outline: API supports creating multiple users
    Given create a User entity with nickName "<nickName>" and uidcid "<uidcid>"
    Then we can count the number "<number>" of users and retrieve the information and reset the database cleanup "<cleanup>"
    Examples:
      |nickName |uidcid |number |cleanup |
      |         |       |0      |false   |
      |Pris     |vsr94  |1      |false   |
      |         |       |1      |false   |
      |Alan     |vsr95  |2      |false   |
      |Gpe      |gp25   |3      |true    |

  Scenario Outline: API supports creating User with multiple graphs
    Given create a User entity with nickName "Fallen" and uidcid "vsr90"
    When when we create the graph with "<title>" and "<description>"
    Then we can count the number "<number>" of graphs and retrieve the information and reset the database cleanup "<cleanup>"
    Examples:
      |title |description |number |cleanup |
      |      |       |0      |false   |
      |wxaw  |a12ex  |1      |false   |
      |      |       |1      |false   |
      |wddw  |wfrca  |2      |false   |
      |sda   |wg12d  |3      |true    |
