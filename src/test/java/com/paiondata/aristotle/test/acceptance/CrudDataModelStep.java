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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Steps for CRUD operations on the Doctor data model.
 */
public class CrudDataModelStep extends AbstractStepDefinitions {

    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";
    private static final String START_NODE = "startNode";
    private static final String END_NODE = "endNode";
    private static boolean isDatabaseCleaningEnabled = true;
    private String uidcid;
    private String nickName;
    private String graphUuid;

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
                .get(USER_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);

        Assert.assertEquals(Collections.emptyList(), response.jsonPath().get("data"));
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
                .get(USER_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);

        final List<?> ids = response.jsonPath().get(USER_UIDCID_PATH);
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
                .delete(USER_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);
    }

    @Given("create a User entity with nickName {string} and uidcid {string}")
    public void createUserWithNickNameAndUidcid(String nickName, String uidcid) {
        Response response = createUser(nickName, uidcid);
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
                .get(USER_ENDPOINT + "/" + this.uidcid);
        response.then()
                .statusCode(OK_CODE);

        Assert.assertEquals(this.uidcid, response.jsonPath().get(USER_UIDCID_PATH));
        Assert.assertEquals(this.nickName, response.jsonPath().get(USER_NICK_NAME_PATH));
    }

    /**
     * creates a User entity.
     * @param nickName nickName.
     * @param uidcid uidcid.
     * @return Response.
     */
    protected Response createUser(final String nickName, final String uidcid) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-update-user.json"),
                        uidcid, nickName))
                .when()
                .post(USER_ENDPOINT);
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
                    .body(String.format(payload("create-update-user.json"),
                            inputUidcid, inputNickName))
                    .when()
                    .put(USER_ENDPOINT);

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
    @When("when we create the graph with {string} and {string}")
    public void createGraph(final String title, final String description) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-bind-graph.json"),
                        description, title, this.uidcid))
                .when()
                .post(NODE_GRAPH_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);
    }

    @Then("we can query the graph and retrieve the information with {string} and {string}")
    public void weCanQueryTheGraphAndRetrieveTheInformation(final String title, final String description) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(USER_GRAPH_ENDPOINT + "/" + this.uidcid);
        response.then()
                .statusCode(OK_CODE);

        List<String> graphUuids = response.jsonPath().get("data.uuid");
        this.graphUuid = graphUuids.get(0);

        List<String> titles = response.jsonPath().get("data.title");
        Assert.assertEquals(title, titles.get(0));
        List<String> descriptions = response.jsonPath().get("data.description");
        Assert.assertEquals(description, descriptions.get(0));
    }

    @Given("we create a User with {string} and {string} and add a graph with {string} and {string}")
    public void weCreateAUserWithAndAndAddAGraphWithAnd(String nickName, String uidcid, String title, String description) {
        createUser(nickName, uidcid);
        this.uidcid = uidcid;
        this.nickName = nickName;

        createGraph(title, description);
    }

    @And("we update the graph with {string} and {string}")
    public void weUpdateTheGraphWithAnd(String newTitle, String newDescription) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("update-graph.json"),
                        this.graphUuid, newTitle, newDescription))
                .when()
                .put(GRAPH_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);
    }

    @Then("we can count the number {string} of users and retrieve the information and reset the database cleanup {string}")
    public void weCanCountTheNumberOfUsersAndRetrieveTheInformationAndResetTheDatabaseCleanup(final String count, final String inputCleanup) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(USER_ENDPOINT);
        response.then()
                .statusCode(OK_CODE);

        List<String> data = response.jsonPath().get("data");
        Assert.assertEquals(Integer.parseInt(count), data.size());

        isDatabaseCleaningEnabled = Boolean.parseBoolean(inputCleanup);
    }

    @Then("we can count the number {string} of graphs and retrieve the information and reset the database cleanup {string}")
    public void weCanCountTheNumberOfGraphsAndRetrieveTheInformationAndResetTheDatabaseCleanup(final String count, final String inputCleanup) {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(USER_GRAPH_ENDPOINT + "/" + this.uidcid);
        response.then()
                .statusCode(OK_CODE);

        List<String> graphs = response.jsonPath().get("data.graphs");
        Assert.assertEquals(Integer.parseInt(count), graphs.size());

        isDatabaseCleaningEnabled = Boolean.parseBoolean(inputCleanup);
    }

    @When("when we create the graph with {string} and add nodes with {string} and bindings with {string}")
    public void whenWeCreateTheGraphWithAndAddNodesWithAndBindingsWith(String graphInfo, String nodeInfo, String relationInfo) {
        if (!graphInfo.isEmpty() && !nodeInfo.isEmpty() && !relationInfo.isEmpty()) {
            Map<String, String> graphMap = getMap(graphInfo);
            Map<String, String> nodeMap = getMap(nodeInfo);
            Map<String, String> relationMap = getMap(relationInfo);
            final Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(String.format(payload("create-bind-graph-node.json"),
                            graphMap.get(DESCRIPTION), graphMap.get(TITLE), this.uidcid,
                            nodeMap.get("title1"), nodeMap.get("ID1"), nodeMap.get("title2"), nodeMap.get("ID2"),
                            nodeMap.get("title3"), nodeMap.get("ID3"), nodeMap.get("title4"), nodeMap.get("ID4"),
                            relationMap.get("fromId1"), relationMap.get("relation1"), relationMap.get("toId1"),
                            relationMap.get("fromId2"), relationMap.get("relation2"), relationMap.get("toId2"),
                            relationMap.get("fromId3"), relationMap.get("relation3"), relationMap.get("toId3"),
                            relationMap.get("fromId4"), relationMap.get("relation4"), relationMap.get("toId4"),
                            relationMap.get("fromId5"), relationMap.get("relation5"), relationMap.get("toId5"),
                            relationMap.get("fromId6"), relationMap.get("relation6"), relationMap.get("toId6")))
                    .when()
                    .post(NODE_GRAPH_ENDPOINT);
            response.then()
                    .statusCode(OK_CODE);
        }
    }

    @And("we can query the graph with User uidcid")
    public void weCanQueryTheGraphWithUserUidcid() {
        final Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .get(USER_GRAPH_ENDPOINT + "/" + this.uidcid);
        response.then()
                .statusCode(OK_CODE);

        List<String> graphUuids = response.jsonPath().get("data.uuid");
        this.graphUuid = graphUuids.get(0);
    }


    @Then("we can query the graph and nodes and retrieve the information with {string},{string} and {string}")
    public void weCanQueryTheGraphAndNodesAndRetrieveTheInformationWithAnd(String graphInfo, String nodeInfo, String relationInfo) {
        if (!graphInfo.isEmpty() && !nodeInfo.isEmpty() && !relationInfo.isEmpty()) {
            Map<String, String> graphMap = getMap(graphInfo);
            Map<String, String> nodeMap = getMap(nodeInfo);
            Map<String, String> relationMap = getMap(relationInfo);

            final Response response = RestAssured
                    .given()
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .when()
                    .get(GRAPH_ENDPOINT + "/" + this.graphUuid);
            response.then()
                    .statusCode(OK_CODE);

            System.out.println(response.asString());

            Assert.assertEquals(graphMap.get(TITLE), response.jsonPath().get("data.title"));
            Assert.assertEquals(graphMap.get(DESCRIPTION), response.jsonPath().get("data.description"));
            Assert.assertEquals(nodeMap.get("title3"), response.jsonPath().get("data.nodes[0].startNode.title"));
            Assert.assertEquals(nodeMap.get("title4"), response.jsonPath().get("data.nodes[0].endNode.title"));
            Assert.assertEquals(relationMap.get("relation4"), response.jsonPath().get("data.nodes[0].relation.name"));
        }
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
