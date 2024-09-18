# CrudDataModel.feature
Feature: Aristotle Data Entity CRUD Operations

  Scenario: API supports creating a new User
    Given create a User entity with "Mark" and "6b23"
    Then we can query the user and retrieve the information

  Scenario: API supports updating an existing User
    Given create a User entity with "Hook" and "6b47"
    When the User entity is updated with the following changes:
      | uidcid         | nickName          |
      | 6b47           | Mei               |
    Then we can query the user and retrieve the information

  Scenario Outline: API supports creating Graph in one step
    Given create a User entity with "Gambit" and "vsr93"
    When we create the graph with "<title>" and "<description>"
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
    Given create a User entity with "<nickName>" and "<uidcid>"
    Then we can count the number "<number>" of users and retrieve the information and reset the database "<cleanup>"

    Examples:
      | nickName | uidcid | number | cleanup |
      |          |        | 0      | false   |
      | Pris     | vsr94  | 1      | false   |
      |          |        | 1      | false   |
      | Alan     | vsr95  | 2      | false   |
      | Gpe      | gp25   | 3      | true    |

  Scenario Outline: API supports creating User with multiple graphs
    Given create a User entity with "vsr90" and "maklov"
    When we create the graph with "<title>" and "<description>"
    Then we can count the number "<number>" of graphs and retrieve the information and reset the database "<cleanup>"

    Examples:
      | title | description | number | cleanup |
      |       |             | 0      | false   |
      | wxaw  | a12ex       | 1      | false   |
      |       |             | 1      | false   |
      | wddw  | wfrca       | 2      | false   |
      | sda   | wg12d       | 3      | true    |

  Scenario Outline: API supports creating Graph and add nodes in one step
    Given create a User entity with "<nickName>" and "<uidcid>"
    When we create the graph with "<graphInfo>" and add nodes with "<nodeInfo>" and bindings with "<relationInfo>"
    And we can query the graph with User uidcid
    Then we can query the graph and nodes and retrieve the information with "<graphInfo>","<nodeInfo>" and "<relationInfo>"

    Examples:
      | uidcid |nickName | graphInfo                                                       | nodeInfo                                                                                      | relationInfo                                                                                                                                                                                                                                           |
      | vsr98  |Ander    | title: Journey to the West, description: written by Wu Cheng'en | title1: TangMonk, ID1: 1, title2: Wukong, ID2: 2, title3: Pig, ID3: 3, title4: Wujing, ID4: 4 | fromId1: 1, toId1: 2, relation1: master, fromId2: 1, toId2: 3, relation2: master, fromId3: 1, toId3: 3, relation3: master, fromId4: 2, toId4: 3, relation4: brother, fromId5: 2, toId5: 4, relation5: brother, fromId6: 3, toId6: 4, relation6: brother|

  Scenario Outline: API supports adding nodes after graph creation
    Given create a User entity with "<nickName>" and "<uidcid>" and add a graph with "<graphInfo>"
    When we can query the graph with User uidcid
    And we add nodes with "<nodeInfo>" and bindings with "<relationInfo>"
    Then we can query the graph and nodes and retrieve the information with "<graphInfo>","<nodeInfo>" and "<relationInfo>"

    Examples:
      | uidcid |nickName | graphInfo                                                       | nodeInfo                                                                                      | relationInfo                                                                                                                                                                                                                                           |
      | vsr98  |Ander    | title: Journey to the West, description: written by Wu Cheng'en | title1: TangMonk, ID1: 1, title2: Wukong, ID2: 2, title3: Pig, ID3: 3, title4: Wujing, ID4: 4 | fromId1: 1, toId1: 2, relation1: master, fromId2: 1, toId2: 3, relation2: master, fromId3: 1, toId3: 3, relation3: master, fromId4: 2, toId4: 3, relation4: brother, fromId5: 2, toId5: 4, relation5: brother, fromId6: 3, toId6: 4, relation6: brother|

  Scenario Outline: API supports just updating nodes'relation in one request
    Given create a User entity with nickName "<nickName>" and uidcid "<uidcid>", add a graph with info "<graphInfo>", and add nodes with info "<nodeInfo>" and bindings with info "<relationInfo>"
    When we can query the graph
    And we update the relation with "<newRelationInfo>"
    Then we can query the new relation and retrieve the information with "<newRelationInfo>"

    Examples:
      | uidcid |nickName | graphInfo                                         | nodeInfo                                      | relationInfo                                                                          | newRelationInfo |
      | vsr98  |Ander    | title: NBA, description: A basketball competition | title1: Durant, ID1: 1, title2: Curry, ID2: 2 | fromId1: 1, toId1: 2, relation1: team mate, fromId2: 2, toId2: 1, relation2: team mate| friend          |

  Scenario Outline: API supports just deleting nodes'relation in one request
    Given create a User entity with nickName "<nickName>" and uidcid "<uidcid>", add a graph with info "<graphInfo>", and add nodes with info "<nodeInfo>" and bindings with info "<relationInfo>"
    When we can query the graph
    And we delete the relation
    Then we can no longer query the relation

    Examples:
      | uidcid |nickName | graphInfo                                         | nodeInfo                                      | relationInfo                                                                          |
      | vsr98  |Ander    | title: NBA, description: A basketball competition | title1: Durant, ID1: 1, title2: Curry, ID2: 2 | fromId1: 1, toId1: 2, relation1: team mate, fromId2: 2, toId2: 1, relation2: team mate|

  Scenario Outline: API supports updating and deleting nodes'relation in one request
    Given create a User entity with nickName "<nickName>" and uidcid "<uidcid>", add a graph with info "<graphInfo>", and add nodes with info "<nodeInfo>" and bindings with info "<relationInfo>"
    When we can query the graph
    And we update the relation with "<newRelationInfo>" and delete the another relation
    Then we can query the new relation and retrieve the information with "<newRelationInfo>" and no longer query the deleted relation

    Examples:
      | uidcid |nickName | graphInfo                                         | nodeInfo                                      | relationInfo                                                                          |
      | vsr98  |Ander    | title: NBA, description: A basketball competition | title1: Durant, ID1: 1, title2: Curry, ID2: 2 | fromId1: 1, toId1: 2, relation1: team mate, fromId2: 2, toId2: 1, relation2: team mate|

  Scenario Outline: API supports deleting nodes
    Given create a User entity with nickName "<nickName>" and uidcid "<uidcid>", add a graph with info "<graphInfo>", and add nodes with info "<nodeInfo>" and bindings with info "<relationInfo>"
    When we can query the graph
    And we delete all nodes
    Then we can no longer query the nodes

    Examples:
      | uidcid |nickName | graphInfo                                         | nodeInfo                                      | relationInfo                                                                          |
      | vsr98  |Ander    | title: NBA, description: A basketball competition | title1: Durant, ID1: 1, title2: Curry, ID2: 2 | fromId1: 1, toId1: 2, relation1: team mate, fromId2: 2, toId2: 1, relation2: team mate|

  Scenario Outline: API supports deleting graph
    Given create a User entity with nickName "<nickName>" and uidcid "<uidcid>", add a graph with info "<graphInfo>", and add nodes with info "<nodeInfo>" and bindings with info "<relationInfo>"
    When we can query the graph
    And we delete the graph
    Then we can no longer query the graph

    Examples:
      | uidcid |nickName | graphInfo                                         | nodeInfo                                      | relationInfo                                                                          |
      | vsr98  |Ander    | title: NBA, description: A basketball competition | title1: Durant, ID1: 1, title2: Curry, ID2: 2 | fromId1: 1, toId1: 2, relation1: team mate, fromId2: 2, toId2: 1, relation2: team mate|
