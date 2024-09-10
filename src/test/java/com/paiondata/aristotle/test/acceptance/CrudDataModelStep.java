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
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Steps for CRUD operations on the Doctor data model.
 */
public class CrudDataModelStep extends AbstractStepDefinitions {

    private static boolean isDatabaseCleaningEnabled = true;
    private String uidcid;
    private String nickName;

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

    @Given("there exists a User entity with nickName {string} and uidcid {string}")
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
                .body(String.format(payload("create-doctor-once.json"),
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
                    .body(String.format(payload("create-doctor-once.json"),
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
    public void weCreateTheGraph(final String title, final String description) {
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
                .get(GRAPH_ENDPOINT + "/" + title);
        response.then()
                .statusCode(OK_CODE);

        Assert.assertEquals(title, response.jsonPath().get("data.title"));
    }
}
