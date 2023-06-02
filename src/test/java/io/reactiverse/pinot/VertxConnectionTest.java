/*
 *  Copyright 2023 The original authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.reactiverse.pinot;

import org.apache.pinot.client.ResultSet;
import org.apache.pinot.client.ResultSetGroup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
@Testcontainers
public class VertxConnectionTest {

    private Vertx vertx = Vertx.vertx();

    // start docker container prior to tests using `docker run -p 8000:8000 -p 9000:9000 apachepinot/pinot:0.12.0 QuickStart -type batch`
    // image takes time to load tables and testcontainers does not account for that.

    // @Container
    // public static GenericContainer<?> pinot = new GenericContainer<>(DockerImageName.parse("apachepinot/pinot:0.12.0"))
    // .withCommand("QuickStart -type batch")
    // .withExposedPorts(8000, 9000);

    @AfterEach
    public void tearDown(VertxTestContext testContext) {
        vertx.close(testContext.succeedingThenComplete());
    }

    private void checkGetAssertions(VertxTestContext testContext, ResultSetGroup resultSetGroup) {
        ResultSet results = resultSetGroup.getResultSet(0);
        testContext.verify(() -> {
            assertEquals(results.getColumnName(0), "playerName");
            assertEquals(results.getColumnName(1), "totalHomeRuns");
            assertEquals(results.getRowCount(), 2);

            assertEquals(results.getString(0, 0), "Barry Lamar");
            assertEquals(results.getDouble(0, 1), 762.0);
            assertEquals(results.getString(1, 0), "Henry Louis");
            assertEquals(results.getDouble(1, 1), 755.0);
            testContext.completeNow();
        });
    }

    @Test
    public void testGetPlayers(VertxTestContext testContext) {
        String brokerUrl = "localhost:8000";
        VertxConnection connection = VertxConnectionFactory.fromHostList(vertx, brokerUrl);

        String query = "select playerName, sum(homeRuns) AS totalHomeRuns from baseballStats where homeRuns > 0 group by playerID, playerName ORDER BY totalHomeRuns DESC limit 2";

        connection
                .execute(query)
                .onSuccess(resultSetGroup -> checkGetAssertions(testContext, resultSetGroup));
    }

    @Test
    public void testPreparedStatement(VertxTestContext testContext) {
        String brokerUrl = "localhost:8000";
        VertxConnection connection = VertxConnectionFactory.fromHostList(vertx, brokerUrl);

        String query = "select playerName, sum(homeRuns) AS totalHomeRuns from baseballStats where homeRuns > ? group by playerID, playerName ORDER BY totalHomeRuns DESC limit 2";
        connection
                .prepareStatement(query)
                .setInt(0, 0)
                .execute()
                .onSuccess(resultSetGroup -> checkGetAssertions(testContext, resultSetGroup));
    }

}
