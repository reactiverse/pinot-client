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
package io.reactiverse.pinot.client.reactivex;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.pinot.client.ResultSet;
import org.apache.pinot.client.ResultSetGroup;
import org.apache.pinot.client.VertxPinotClientTransport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
@Testcontainers
public class VertxConnectionTest {

    private Vertx vertx = Vertx.vertx();

    @Container
    public static GenericContainer<?> pinot = new GenericContainer<>(DockerImageName.parse("apachepinot/pinot:0.12.0"))
            .withCommand("QuickStart -type batch")
            .withExposedPorts(2181, 8000, 9000)
            .waitingFor(Wait.forLogMessage(".*Offline quickstart setup complete.*\\n", 1))
            .withStartupTimeout(Duration.ofSeconds(600))
            .withReuse(true);

    @AfterEach
    public void tearDown(VertxTestContext testContext) {
        vertx.getDelegate().close(testContext.succeedingThenComplete());
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

    private void executeQueryTest(VertxTestContext testContext, VertxConnection connection) {
        String query = "select playerName, sum(homeRuns) AS totalHomeRuns from baseballStats where homeRuns > 0 group by playerID, playerName ORDER BY totalHomeRuns DESC limit 2";

        var disposable = connection
                .rxExecute(query)
                .subscribe(
                        resultSetGroup -> checkGetAssertions(testContext, resultSetGroup),
                        testContext::failNow
                );
    }

    @Test
    public void testGetPlayers(VertxTestContext testContext) {
        String brokerUrl = "localhost:" + pinot.getMappedPort(8000);
        VertxConnection connection = VertxConnectionFactory.fromHostList(vertx, new String[]{brokerUrl});
        executeQueryTest(testContext, connection);
    }

    @Test
    public void testPreparedStatement(VertxTestContext testContext) {
        String brokerUrl = "localhost:" + pinot.getMappedPort(8000);
        VertxConnection connection = VertxConnectionFactory.fromHostList(vertx, new String[]{brokerUrl});

        String query = "select playerName, sum(homeRuns) AS totalHomeRuns from baseballStats where homeRuns > ? group by playerID, playerName ORDER BY totalHomeRuns DESC limit 2";
        var disposable = connection
                .prepareStatement(query)
                .setInt(0, 0)
                .rxExecute()
                .subscribe(
                        resultSetGroup -> checkGetAssertions(testContext, resultSetGroup),
                        testContext::failNow
                );
    }

    @Test
    public void testVertxTransportWithBroker(VertxTestContext testContext) {
        String brokerUrl = "localhost:" + pinot.getMappedPort(8000);
        VertxPinotClientTransport transport = new VertxPinotClientTransport(vertx.getDelegate());
        VertxConnection connection = VertxConnectionFactory.fromHostList(vertx, List.of(brokerUrl), transport);
        executeQueryTest(testContext, connection);
    }

    @Test
    public void testVertxTransportWithController(VertxTestContext testContext) {
        Properties properties = new Properties();
        properties.setProperty("brokerUpdateFrequencyInMillis", "60000");

        String controllerUrl = "localhost:" + pinot.getMappedPort(9000);
        VertxPinotClientTransport transport = new VertxPinotClientTransport(vertx.getDelegate());
        VertxConnection connection = VertxConnectionFactory.fromController(vertx, properties, controllerUrl, transport);

        executeQueryTest(testContext, connection);
    }

    @Test
    @Disabled
    public void testVertxTransportWithZookeeper(VertxTestContext testContext) {
        String zookeeperUrl = "localhost:" + pinot.getMappedPort(2181) + "/QuickStartCluster";
        VertxPinotClientTransport transport = new VertxPinotClientTransport(vertx.getDelegate());
        VertxConnection connection = VertxConnectionFactory.fromZookeeper(vertx, zookeeperUrl, transport);
        executeQueryTest(testContext, connection);
    }

}
