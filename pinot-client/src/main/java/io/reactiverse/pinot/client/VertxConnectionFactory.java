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
package io.reactiverse.pinot.client;

import java.util.List;
import java.util.Properties;

import org.apache.pinot.client.*;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Vertx;

@VertxGen
public interface VertxConnectionFactory {
    /**
     * Creates a connection to a Pinot cluster, given its Zookeeper URL
     *
     * @param zkUrl The URL to the Zookeeper cluster, must include the cluster name e.g host:port/chroot/pinot-cluster
     * @return A connection that connects to the brokers in the given Helix cluster
     */
    static VertxConnection fromZookeeper(Vertx vertx, String zkUrl) {
        Connection connection = ConnectionFactory.fromZookeeper(zkUrl);
        return new VertxConnectionImpl(vertx, connection);
    }

    /**
     * Creates a connection to a Pinot cluster, given its Zookeeper URL
     *
     * @param zkUrl     The URL to the Zookeeper cluster, must include the cluster name e.g
     *                  host:port/chroot/pinot-cluster
     * @param transport pinot transport
     * @return A connection that connects to the brokers in the given Helix cluster
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    static VertxConnection fromZookeeper(Vertx vertx, String zkUrl, PinotClientTransport transport) {
        try {
            Connection connection = ConnectionFactory.fromZookeeper(zkUrl, transport);
            return new VertxConnectionImpl(vertx, connection);
        } catch (Exception e) {
            throw new PinotClientException(e);
        }
    }

    /**
     * Creates a connection to a Pinot cluster, given its Zookeeper URL and properties.
     *
     * @param properties connection properties
     * @param zkUrl      zookeeper URL.
     * @return A connection that connects to the brokers in the given Helix cluster
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    static VertxConnection fromZookeeper(Vertx vertx, Properties properties, String zkUrl) {
        try {
            Connection connection = ConnectionFactory.fromZookeeper(properties, zkUrl);
            return new VertxConnectionImpl(vertx, connection);
        } catch (Exception e) {
            throw new PinotClientException(e);
        }
    }

    /**
     * @param controllerUrl url host:port of the controller
     * @return A connection that connects to brokers as per the given controller
     */
    static VertxConnection fromController(Vertx vertx, String controllerUrl) {
        return fromController(vertx, new Properties(), controllerUrl);
    }

    /**
     * @param properties
     * @param controllerUrl url host:port of the controller
     * @return A connection that connects to brokers as per the given controller
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    static VertxConnection fromController(Vertx vertx, Properties properties, String controllerUrl) {
        try {
            Connection pinotConnection = ConnectionFactory.fromController(properties, controllerUrl);
            return new VertxConnectionImpl(vertx, pinotConnection);
        } catch (Exception e) {
            throw new PinotClientException(e);
        }
    }

    /**
     * @param properties
     * @param controllerUrl url host:port of the controller
     * @param transport     pinot transport
     * @return A connection that connects to brokers as per the given controller
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    static VertxConnection fromController(Vertx vertx, Properties properties, String controllerUrl, PinotClientTransport transport) {
        try {
            Connection pinotConnection = ConnectionFactoryBridge.fromController(properties, controllerUrl, transport);
            return new VertxConnectionImpl(vertx, pinotConnection);
        } catch (Exception e) {
            throw new PinotClientException(e);
        }
    }

    /**
     * Creates a connection to a Pinot cluster, given its Zookeeper URL
     *
     * @param properties The Pinot connection properties
     * @param zkUrl      The URL to the Zookeeper cluster, must include the cluster name e.g
     *                   host:port/chroot/pinot-cluster
     * @param transport  pinot transport
     * @return A connection that connects to the brokers in the given Helix cluster
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    static VertxConnection fromZookeeper(Vertx vertx, Properties properties, String zkUrl, PinotClientTransport transport) {
        try {
            Connection connection = ConnectionFactory.fromZookeeper(properties, zkUrl, transport);
            return new VertxConnectionImpl(vertx, connection);
        } catch (Exception e) {
            throw new PinotClientException(e);
        }
    }

    /**
     * Creates a connection from properties containing the connection parameters.
     *
     * @param properties The properties to use for the connection
     * @return A connection that connects to the brokers specified in the properties
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    static VertxConnection fromProperties(Vertx vertx, Properties properties) {
        Connection connection = ConnectionFactory.fromProperties(properties);
        return new VertxConnectionImpl(vertx, connection);
    }

    /**
     * Creates a connection from properties containing the connection parameters.
     *
     * @param properties The properties to use for the connection
     * @param transport  pinot transport
     * @return A connection that connects to the brokers specified in the properties
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    static VertxConnection fromProperties(Vertx vertx, Properties properties, PinotClientTransport transport) {
        Connection connection = ConnectionFactory.fromProperties(properties, transport);
        return new VertxConnectionImpl(vertx, connection);
    }

    /**
     * Creates a connection which sends queries randomly between the specified brokers.
     *
     * @param brokers The list of brokers to send queries to
     * @return A connection to the set of brokers specified
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    static VertxConnection fromHostList(Vertx vertx, String... brokers) {
        Connection connection = ConnectionFactory.fromHostList(brokers);
        return new VertxConnectionImpl(vertx, connection);
    }

    /**
     * Creates a connection which sends queries randomly between the specified brokers.
     *
     * @param brokers   The list of brokers to send queries to
     * @param transport pinot transport
     * @return A connection to the set of brokers specified
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    static VertxConnection fromHostList(Vertx vertx, List<String> brokers, PinotClientTransport transport) {
        Connection pinotConnection = ConnectionFactory.fromHostList(brokers, transport);
        return new VertxConnectionImpl(vertx, pinotConnection);
    }

    /**
     * Creates a connection which sends queries randomly between the specified brokers.
     *
     * @param properties The Pinot connection properties
     * @param brokers    The list of brokers to send queries to
     * @param transport  pinot transport
     * @return A connection to the set of brokers specified
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    static VertxConnection fromHostList(Vertx vertx, Properties properties, List<String> brokers, PinotClientTransport transport) {
        Connection pinotConnection = ConnectionFactory.fromHostList(properties, brokers, transport);
        return new VertxConnectionImpl(vertx, pinotConnection);
    }

}
