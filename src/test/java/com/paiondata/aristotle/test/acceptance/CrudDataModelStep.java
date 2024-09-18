/*
 * Copyright 2024 Paion Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paiondata.aristotle.test.acceptance;

import com.paiondata.aristotle.test.common.base.Constants;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Steps for CRUD operations on the Doctor data model.
 */
public class CrudDataModelStep extends AbstractStepDefinitions {

    private static boolean isDatabaseCleaningEnabled = true;
    private String uidcid;
    private String nickName;
    private String graphUuid;
    private String relationUuid1;
    private String relationUuid2;
    private String nodeUuid1;
    private String nodeUuid2;

    /**
     * Check if the database is empty.
     */
    @Before
    public void checkDatabase() {
        if (!isDatabaseCleaningEnabled) {
            return;
        }
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.USER_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);

        Assert.assertEquals(Collections.emptyList(), response.jsonPath().get(Constants.DATA));
    }

    /**
     * Clean up the database.
     */
    @After
    public void cleanDatabase() {
        if (!isDatabaseCleaningEnabled) {
            return;
        }
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.USER_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);

        final List<?> ids = response.jsonPath().get(Constants.USER_UIDCID_PATH);
        aCreatedDoctorEntityIsDeleted(ids);
    }

    /**
     * Delete the Doctor entity.
     * @param ids The IDs of the Doctor entities to delete.
     */
    public void aCreatedDoctorEntityIsDeleted(final List<?> ids) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(ids)
                .when()
                .delete(Constants.USER_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);
    }

    /**
     * Create a User entity.
     * @param nickName nickName.
     * @param uidcid uidcid.
     */
    @Given("create a User entity with {string} and {string}")
    public void createUserWithNickNameAndUidcid(final String nickName, final String uidcid) {
        final Response response = createUser(nickName, uidcid);
        this.uidcid = uidcid;
        this.nickName = nickName;
    }

    /**
     * Verify the User entity.
     */
    @Then("we can query the user and retrieve the information")
    public void weCanQueryTheDoctorAndRetrieveTheInformation() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.USER_ENDPOINT + Constants.SLASH + this.uidcid);
        response.then()
                .statusCode(OK_CODE);

        Assert.assertEquals(this.uidcid, response.jsonPath().get(Constants.USER_UIDCID_PATH));
        Assert.assertEquals(this.nickName, response.jsonPath().get(Constants.USER_NICK_NAME_PATH));
    }

    /**
     * creates a User entity.
     * @param nickName nickName.
     * @param uidcid uidcid.
     *
     * @return Response.
     */
    protected Response createUser(final String nickName, final String uidcid) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(Constants.CREATE_UPDATE_USER_JSON),
                        uidcid, nickName))
                .when()
                .post(Constants.USER_ENDPOINT);
    }

    /**
     * update the User entity.
     * @param dataTable the data table.
     */
    @When("the User entity is updated with the following changes:")
    public void theUserEntityIsUpdatedWithTheFollowingChanges(final DataTable dataTable) {
        final List<Map<String, String>> details = dataTable.asMaps();

        for (final Map<String, String> detail : details) {
            final String inputUidcid = detail.get("uidcid");
            final String inputNickName = detail.get("nickName");

            final Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(String.format(payload(Constants.CREATE_UPDATE_USER_JSON),
                            inputUidcid, inputNickName))
                    .when()
                    .put(Constants.USER_ENDPOINT);

            this.uidcid = inputUidcid;
            this.nickName = inputNickName;

            response.then()
                    .statusCode(OK_CODE);
        }
    }

    /**
     * we can create the graph.
     * @param title the title.
     * @param description the description.
     */
    @When("we create the graph with {string} and {string}")
    public void createGraph(final String title, final String description) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload(Constants.CREATE_BIND_GRAPH_JSON),
                        description, title, this.uidcid))
                .when()
                .post(Constants.NODE_GRAPH_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);
    }

    /**
     * we can query the graph and retrieve the information.
     * @param title the title.
     * @param description the description.
     */
    @Then("we can query the graph and retrieve the information with {string} and {string}")
    public void weCanQueryTheGraphAndRetrieveTheInformation(final String title, final String description) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.USER_GRAPH_ENDPOINT + Constants.SLASH + this.uidcid);
        response.then()
                .statusCode(OK_CODE);

        final List<String> graphUuids = response.jsonPath().get(Constants.UUID_PATH);
        this.graphUuid = graphUuids.get(0);

        final List<String> titles = response.jsonPath().get(Constants.TITLE_PATH);
        Assert.assertEquals(title, titles.get(0));
        final List<String> descriptions = response.jsonPath().get(Constants.DESCRIPTION_PATH);
        Assert.assertEquals(description, descriptions.get(0));
    }

    /**
     * create a User with {string} and {string} and add a graph with {string} and {string}").
     * @param nickName the nickName
     * @param uidcid the uidcid
     * @param title the title
     * @param description the description
     */
    @Given("we create a User with {string} and {string} and add a graph with {string} and {string}")
    public void weCreateAUserWithAndAndAddAGraphWithAnd(final String nickName, final String uidcid,
                                                        final String title, final String description) {
        createUser(nickName, uidcid);
        this.uidcid = uidcid;
        this.nickName = nickName;

        createGraph(title, description);
    }

    /**
     * we update the graph with {string} and {string}.
     * @param newTitle the title
     * @param newDescription the description
     */
    @And("we update the graph with {string} and {string}")
    public void weUpdateTheGraphWithAnd(final String newTitle, final String newDescription) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("update-graph.json"),
                        this.graphUuid, newTitle, newDescription))
                .when()
                .put(Constants.GRAPH_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);
    }

    /**
     * we can count the number {string} of users and retrieve the information and reset the database {string}.
     * @param count the count
     * @param inputCleanup true or false
     */
    @Then("we can count the number {string} of users and retrieve the information and reset the database {string}")
    public void countTheNumberOfUsersRetrieveTheInformation(final String count, final String inputCleanup) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.USER_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);

        final List<String> data = response.jsonPath().get(Constants.DATA);
        Assert.assertEquals(Integer.parseInt(count), data.size());

        isDatabaseCleaningEnabled = Boolean.parseBoolean(inputCleanup);
    }

    /**
     * we can count the number {string} of graphs and retrieve the information and reset the database {string}.
     * @param count the count
     * @param inputCleanup true or false
     */
    @Then("we can count the number {string} of graphs and retrieve the information and reset the database {string}")
    public void countTheNumberOfGraphsRetrieveTheInformation(final String count, final String inputCleanup) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.USER_GRAPH_ENDPOINT + Constants.SLASH + this.uidcid);
        response.then()
                .statusCode(OK_CODE);

        final List<String> graphs = response.jsonPath().get("data.graphs");
        Assert.assertEquals(Integer.parseInt(count), graphs.size());

        isDatabaseCleaningEnabled = Boolean.parseBoolean(inputCleanup);
    }

    /**
     * we create the graph with {string} and add nodes with {string} and bindings with {string}.
     * @param graphInfo the graph info
     * @param nodeInfo the node info
     * @param relationInfo the relation info
     */
    @When("we create the graph with {string} and add nodes with {string} and bindings with {string}")
    public void whenWeCreateTheGraphWithAndAddNodesWithAndBindingsWith(final String graphInfo, final String nodeInfo,
                                                                       final String relationInfo) {
        if (!graphInfo.isEmpty() && !nodeInfo.isEmpty() && !relationInfo.isEmpty()) {
            final Map<String, String> graphMap = getMap(graphInfo);
            final Map<String, String> nodeMap = getMap(nodeInfo);
            final Map<String, String> relationMap = getMap(relationInfo);
            final Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(String.format(payload("create-bind-graph-node.json"),
                            graphMap.get(Constants.DESCRIPTION), graphMap.get(Constants.TITLE), this.uidcid,
                            nodeMap.get("title1"), nodeMap.get("ID1"), nodeMap.get("title2"), nodeMap.get("ID2"),
                            nodeMap.get("title3"), nodeMap.get("ID3"), nodeMap.get("title4"), nodeMap.get("ID4"),
                            relationMap.get("fromId1"), relationMap.get("relation1"), relationMap.get("toId1"),
                            relationMap.get("fromId2"), relationMap.get("relation2"), relationMap.get("toId2"),
                            relationMap.get("fromId3"), relationMap.get("relation3"), relationMap.get("toId3"),
                            relationMap.get("fromId4"), relationMap.get("relation4"), relationMap.get("toId4"),
                            relationMap.get("fromId5"), relationMap.get("relation5"), relationMap.get("toId5"),
                            relationMap.get("fromId6"), relationMap.get("relation6"), relationMap.get("toId6")))
                    .when()
                    .post(Constants.NODE_GRAPH_ENDPOINT);
            response.then()
                    .statusCode(OK_CODE);
        }
    }

    /**
     * we can query the graph with User uidcid.
     */
    @And("we can query the graph with User uidcid")
    public void weCanQueryTheGraphWithUserUidcid() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.USER_GRAPH_ENDPOINT + Constants.SLASH + this.uidcid);
        response.then()
                .statusCode(OK_CODE);

        final List<String> graphUuids = response.jsonPath().get(Constants.UUID_PATH);
        this.graphUuid = graphUuids.get(0);
    }

    /**
     * we can query the graph and nodes and retrieve the information with {string},{string} and {string}.
     * @param graphInfo the graphInfo
     * @param nodeInfo the nodeInfo
     * @param relationInfo the relationInfo
     */
    @Then("we can query the graph and nodes and retrieve the information with {string},{string} and {string}")
    public void weCanQueryTheGraphAndNodesAndRetrieveTheInformationWithAnd(final String graphInfo,
                                                                           final String nodeInfo,
                                                                           final String relationInfo) {
        if (!graphInfo.isEmpty() && !nodeInfo.isEmpty() && !relationInfo.isEmpty()) {
            final Map<String, String> graphMap = getMap(graphInfo);

            final Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .when()
                    .get(Constants.GRAPH_ENDPOINT + Constants.SLASH + this.graphUuid);
            response.then()
                    .statusCode(OK_CODE);

            Assert.assertEquals(graphMap.get(Constants.TITLE), response.jsonPath().get(Constants.TITLE_PATH));
            Assert.assertEquals(graphMap.get(Constants.DESCRIPTION),
                    response.jsonPath().get(Constants.DESCRIPTION_PATH));
        }
    }

    /**
     * create a User entity with {string} and {string} and add a graph with {string}.
     * @param nickName the nickname
     * @param uidcid the uidcid
     * @param graphInfo to add
     */
    @Given("create a User entity with {string} and {string} and add a graph with {string}")
    public void createAUserEntityWithAndAndAddAGraphWith(final String nickName, final String uidcid,
                                                         final String graphInfo) {
        createUserWithNickNameAndUidcid(nickName, uidcid);

        if (!graphInfo.isEmpty()) {
            final Map<String, String> graphMap = getMap(graphInfo);
            final Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(String.format(payload(Constants.CREATE_BIND_GRAPH_JSON),
                            graphMap.get(Constants.DESCRIPTION), graphMap.get(Constants.TITLE), this.uidcid))
                    .when()
                    .post(Constants.NODE_GRAPH_ENDPOINT);
            response.then()
                    .statusCode(OK_CODE);
        }
    }

    /**
     * we add nodes with {string} and bindings with {string}.
     * @param nodeInfo to add
     * @param relationInfo to add
     */
    @When("we add nodes with {string} and bindings with {string}")
    public void weAddNodesWithAndBindingsWith(final String nodeInfo, final String relationInfo) {
        if (!nodeInfo.isEmpty() && !relationInfo.isEmpty()) {
            final Map<String, String> nodeMap = getMap(nodeInfo);
            final Map<String, String> relationMap = getMap(relationInfo);
            final Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(String.format(payload("create-bind-node.json"),
                            this.graphUuid,
                            nodeMap.get(Constants.TITLE1), nodeMap.get(Constants.ID1),
                            nodeMap.get(Constants.TITLE2), nodeMap.get(Constants.ID2),
                            nodeMap.get(Constants.TITLE3), nodeMap.get(Constants.ID3),
                            nodeMap.get(Constants.TITLE4), nodeMap.get(Constants.ID4),
                            relationMap.get(Constants.FROM_ID1), relationMap.get(Constants.RELATION_1),
                            relationMap.get(Constants.TO_ID1), relationMap.get(Constants.FROM_ID2),
                            relationMap.get(Constants.RELATION_2), relationMap.get(Constants.TO_ID2),
                            relationMap.get(Constants.FROM_ID3), relationMap.get(Constants.RELATION_3),
                            relationMap.get(Constants.TO_ID3), relationMap.get(Constants.FROM_ID4),
                            relationMap.get(Constants.RELATION_4), relationMap.get(Constants.TO_ID4),
                            relationMap.get(Constants.FROM_ID5), relationMap.get(Constants.RELATION_5),
                            relationMap.get(Constants.TO_ID5), relationMap.get(Constants.FROM_ID6),
                            relationMap.get(Constants.RELATION_6), relationMap.get(Constants.TO_ID6)))
                    .when()
                    .post(Constants.NODE_ENDPOINT);
            response.then()
                    .statusCode(OK_CODE);
        }
    }

    /**
     * create a User entity with nickName {string} and uidcid {string}, add a graph with info {string}.
     * @param nickName the nickname
     * @param uidcid the uidcid
     * @param graphInfo the graph info
     * @param nodeInfo the node info
     * @param relationInfo the relation info
     */
    @Given("create a User entity with nickName {string} and uidcid {string}, add a graph with info {string}," +
            " and add nodes with info {string} and bindings with info {string}")
    public void createUserAddAGraphAddNodesBindings(final String nickName, final String uidcid, final String graphInfo,
                                                    final String nodeInfo, final String relationInfo) {
        createUserWithNickNameAndUidcid(nickName, uidcid);

        if (!graphInfo.isEmpty() && !nodeInfo.isEmpty() && !relationInfo.isEmpty()) {
            final Map<String, String> graphMap = getMap(graphInfo);
            final Map<String, String> nodeMap = getMap(nodeInfo);
            final Map<String, String> relationMap = getMap(relationInfo);
            final Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(String.format(payload("create-bind-graph-node-1.json"),
                            graphMap.get(Constants.DESCRIPTION), graphMap.get(Constants.TITLE), this.uidcid,
                            nodeMap.get(Constants.TITLE1), nodeMap.get(Constants.ID1), nodeMap.get(Constants.TITLE2),
                            nodeMap.get(Constants.ID2), relationMap.get(Constants.FROM_ID1),
                            relationMap.get(Constants.RELATION_1), relationMap.get(Constants.TO_ID1),
                            relationMap.get(Constants.FROM_ID2), relationMap.get(Constants.RELATION_2),
                            relationMap.get(Constants.TO_ID2)))
                    .when()
                    .post(Constants.NODE_GRAPH_ENDPOINT);
            response.then()
                    .statusCode(OK_CODE);
        }
    }

    /**
     * we can query the graph with user {string} and uidcid {string} and get the relation uuid.
     */
    @When("we can query the graph")
    public void weCanQueryTheGraphWithUserUidcidAndGetTheRelationUuid() {
        weCanQueryTheGraphWithUserUidcid();

        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.GRAPH_ENDPOINT + Constants.SLASH + this.graphUuid);
        response.then()
                .statusCode(OK_CODE);

        final List<String> relationUuids = Arrays.asList(response.jsonPath().get("data.nodes[0].relation.uuid"),
                response.jsonPath().get("data.nodes[1].relation.uuid"));
        this.relationUuid1 = relationUuids.get(0);
        this.relationUuid2 = relationUuids.get(1);

        final List<String> nodeUuids = Arrays.asList(response.jsonPath().get("data.nodes[0].startNode.uuid"),
                response.jsonPath().get("data.nodes[1].startNode.uuid"));
        this.nodeUuid1 = nodeUuids.get(0);
        this.nodeUuid2 = nodeUuids.get(1);
    }

    /**
     * we update the relation with {string}.
     * @param newRelationInfo the new relation info
     */
    @And("we update the relation with {string}")
    public void weUpdateTheRelationWith(final String newRelationInfo) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("update-relation.json"),
                        this.graphUuid, this.relationUuid1, newRelationInfo))
                .when()
                .put(Constants.NODE_RELATE_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);
    }

    /**
     * we can query the new relation and retrieve the information with {string}.
     * @param newRelationInfo the new relation info
     */
    @Then("we can query the new relation and retrieve the information with {string}")
    public void weCanQueryTheNewRelationAndRetrieveTheInformationWith(final String newRelationInfo) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.GRAPH_ENDPOINT + Constants.SLASH + this.graphUuid);
        response.then()
                .statusCode(OK_CODE);

        final String relationName = response.jsonPath().get(Constants.RELATION_NAME_PATH);

        Assert.assertEquals(newRelationInfo, relationName);
    }

    /**
     * we delete the relation.
     */
    @And("we delete the relation")
    public void weDeleteTheRelation() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("delete-relation.json"),
                        this.graphUuid, this.relationUuid2))
                .when()
                .put(Constants.NODE_RELATE_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);
    }

    /**
     * we can no longer query the relation.
     */
    @Then("we can no longer query the relation")
    public void weCanNoLongerQueryTheRelation() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.GRAPH_ENDPOINT + Constants.SLASH + this.graphUuid);
        response.then()
                .statusCode(OK_CODE);

        final Object relation = response.jsonPath().get(Constants.RELATION_PATH);

        Assert.assertNull(relation);
    }

    /**
     * we update the relation with {string} and delete the another relation.
     * @param newRelationInfo the new relation info
     */
    @And("we update the relation with {string} and delete the another relation")
    public void weUpdateTheRelationWithAndDeleteTheAnotherRelation(final String newRelationInfo) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("update-delete-relation.json"),
                        this.graphUuid, this.relationUuid1, newRelationInfo, this.relationUuid2))
                .when()
                .put(Constants.NODE_RELATE_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);
    }

    /**
     * we can query the new relation and retrieve the information with {string} and no longer query deleted relation.
     * @param newRelationInfo the new relation info
     */
    @Then("we can query the new relation and retrieve the information " +
            "with {string} and no longer query the deleted relation")
    public void queryTheNewRelationAndRetrieveTheInformation(final String newRelationInfo) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.GRAPH_ENDPOINT + Constants.SLASH + this.graphUuid);
        response.then()
                .statusCode(OK_CODE);

        final String relationName = response.jsonPath().get(Constants.RELATION_NAME_PATH);
        final Object relation = response.jsonPath().get(Constants.RELATION_PATH);

        Assert.assertEquals(newRelationInfo, relationName);
        Assert.assertNull(relation);
    }

    /**
     * we delete all nodes.
     */
    @And("we delete all nodes")
    public void weDeleteAllNodes() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("delete-node.json"),
                        this.nodeUuid1, this.nodeUuid2))
                .when()
                .delete(Constants.NODE_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);
    }

    /**
     * we can no longer query the nodes.
     */
    @Then("we can no longer query the nodes")
    public void weCanNoLongerQueryTheNodes() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.GRAPH_ENDPOINT + Constants.SLASH + this.graphUuid);
        response.then()
                .statusCode(OK_CODE);

        final List<Map<String, Object>> nodes = response.jsonPath().getList("data.nodes");

        for (final Map<String, Object> node : nodes) {
            final Object startNode = node.get("startNode");

            Assert.assertTrue("StartNode should be an empty object",
                    startNode instanceof Map && ((Map<?, ?>) startNode).isEmpty());
        }
    }

    /**
     * we delete the graph.
     */
    @And("we delete the graph")
    public void weDeleteTheGraph() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("delete-graph.json"),
                        this.graphUuid))
                .when()
                .delete(Constants.GRAPH_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);
    }

    /**
     * we can no longer query the graph.
     */
    @Then("we can no longer query the graph")
    public void weCanNoLongerQueryTheGraph() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(Constants.GRAPH_ENDPOINT + Constants.SLASH + this.graphUuid);
        response.then()
                .statusCode(OK_CODE);

        final Object graph = response.jsonPath().get("data.nodes[0]");

        Assert.assertNull(graph);
    }

    /**
     * get the map.
     * @param info the info.
     * @return Map.
     */
    private static Map<String, String> getMap(final String info) {
        final Map<String, String> map = new HashMap<>();

        final String[] pairs = info.split(", ");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split(": ");
            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }
}
