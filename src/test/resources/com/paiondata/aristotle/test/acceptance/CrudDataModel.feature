# CrudDataModel.feature
Feature: Aristotle Data Entity CRUD Operations

  Scenario: Create a New User
    Given there exists a User entity with nickName "Mark" and uidcid "6b23"
    Then we can query the user and retrieve the information