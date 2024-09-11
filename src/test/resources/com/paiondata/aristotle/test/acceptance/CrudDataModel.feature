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

  Scenario Outline: API supports creating Graph in one step
    Given create a User entity with nickName "Gambit" and uidcid "vsr93"
    When when we create the graph with "<title>" and "<description>"
    Then we can query the graph and retrieve the information with "<title>" and "<description>"

    Examples:
      | title               | description            |
      | Journey to the West | written by Wu Cheng'en |

  Scenario Outline: API supports updating an existing Graph
    Given we create a User with "<nickName>" and "<uidcid>" and add a graph with "<title>" and "<description>"
    When we can query the graph and retrieve the information with "<title>" and "<description>"
    And we update the graph with "<newTitle>" and "<newDescription>"
    Then we can query the graph and retrieve the information with "<newTitle>" and "<newDescription>"

    Examples:
      | nickName | uidcid | title               | description            | newTitle            |newDescription |
      | Alpha    | vsr97  | Journey to the West | written by Wu Cheng'en | Journey to the East |Dan'e          |

  Scenario Outline: API supports creating multiple users
    Given create a User entity with nickName "<nickName>" and uidcid "<uidcid>"
    Then we can count the number "<number>" of users and retrieve the information and reset the database cleanup "<cleanup>"

    Examples:
      | nickName | uidcid | number | cleanup |
      |          |        | 0      | false   |
      | Pris     | vsr94  | 1      | false   |
      |          |        | 1      | false   |
      | Alan     | vsr95  | 2      | false   |
      | Gpe      | gp25   | 3      | true    |

  Scenario Outline: API supports creating User with multiple graphs
    Given create a User entity with nickName "Fallen" and uidcid "vsr90"
    When when we create the graph with "<title>" and "<description>"
    Then we can count the number "<number>" of graphs and retrieve the information and reset the database cleanup "<cleanup>"

    Examples:
      | title | description | number | cleanup |
      |       |             | 0      | false   |
      | wxaw  | a12ex       | 1      | false   |
      |       |             | 1      | false   |
      | wddw  | wfrca       | 2      | false   |
      | sda   | wg12d       | 3      | true    |

  Scenario Outline: API supports creating Graph and add nodes in one step
    Given create a User entity with nickName "Gambit" and uidcid "vsr93"
    When when we create the graph with "<graphInfo>" and add nodes with "<nodeInfo>" and bindings with "<relationInfo>"
    And we can query the graph with User uidcid
    Then we can query the graph and nodes and retrieve the information with "<graphInfo>","<nodeInfo>" and "<relationInfo>"

    Examples:
      | graphInfo                                                       | nodeInfo                                                                                       | relationInfo |
      | title: Journey to the West, description: written by Wu Cheng'en | title1: TangMonk, ID1: 1, title2: Wukong, ID2: 2, title3: Pig, ID3: 3, title4: Wujing, ID4: 4 | fromId1: 1, toId1: 2, relation1: master, fromId2: 1, toId2: 3, relation2: master, fromId3: 1, toId3: 3, relation3: master, fromId4: 2, toId4: 3, relation4: brother, fromId5: 2, toId5: 4, relation5: brother, fromId6: 3, toId6: 4, relation6: brother|
