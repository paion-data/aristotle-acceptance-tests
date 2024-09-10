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

import org.junit.Assert;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Collections;

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

    @Given("there exists a User entity with nickName {string} and uidcid {string}")
    public void createUserWithNickNameAndUidcid(String arg0, String arg1) {

    }

    /**
     * creates a User entity.
     * @param uidcid uidcid.
     * @param nickName nickName.
     * @return Response.
     */
    protected Response createUser(final String uidcid, final String nickName) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(String.format(payload("create-doctor-once.json"),
                        uidcid, nickName))
                .when()
                .post(USER_ENDPOINT);
    }
}
