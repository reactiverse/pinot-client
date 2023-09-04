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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.pinot.client.BrokerResponse;
import org.apache.pinot.client.PinotClientException;
import org.apache.pinot.client.PinotClientTransport;
import org.apache.pinot.client.Request;
import org.apache.pinot.spi.utils.CommonConstants;
import org.apache.pinot.spi.utils.JsonUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectReader;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

public class VertxPinotClientTransport implements PinotClientTransport {
    private static final ObjectReader OBJECT_READER = JsonUtils.DEFAULT_READER;

    private static final String DEFAULT_EXTRA_QUERY_OPTION_STRING = "groupByMode=sql;responseFormat=sql";
    private final WebClient client;
    private final Map<String, String> headers;
    private final String scheme;
    private final String extraOptions;
    private final int brokerReadTimeout;

    public VertxPinotClientTransport(Vertx vertx) {
        this(vertx, null, null, null, null);
    }

    public VertxPinotClientTransport(Vertx vertx, Map<String, String> headers, String scheme, String extraOptionString,
                                     WebClientOptions options) {
        this.headers = Objects.requireNonNullElseGet(headers, HashMap::new);
        this.scheme = Objects.requireNonNullElse(scheme, CommonConstants.HTTP_PROTOCOL);
        this.extraOptions = StringUtils.isEmpty(extraOptionString) ? DEFAULT_EXTRA_QUERY_OPTION_STRING : extraOptionString;
        options = Objects.requireNonNullElseGet(options, WebClientOptions::new);
        this.client = WebClient.create(vertx, options.setLogActivity(true));
        this.brokerReadTimeout = options.getIdleTimeout();
    }

    @Override
    public BrokerResponse executeQuery(String brokerAddress, String query) throws PinotClientException {
        try {
            return executeQueryAsync(brokerAddress, query).get(brokerReadTimeout, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {
            throw new PinotClientException(e);
        }
    }

    @Override
    public CompletableFuture<BrokerResponse> executeQueryAsync(String brokerAddress, String query) {
        try {
            JsonObject json = new JsonObject()
                    .put("sql", query)
                    .put("queryOptions", extraOptions);

            String url = scheme + "://" + brokerAddress + "/query/sql";

            return client
                    .postAbs(url)
                    .putHeaders(MultiMap.caseInsensitiveMultiMap().addAll(headers))
                    .putHeader("Content-Type", "application/json; charset=utf-8")
                    .sendJsonObject(json)
                    .map(response -> {
                        String object = response.bodyAsString();
                        try {
                            return BrokerResponse.fromJson(OBJECT_READER.readTree(object));
                        }
                        catch (JsonProcessingException e) {
                            throw new PinotClientException(e);
                        }
                    })
                    .toCompletionStage()
                    .toCompletableFuture();
        }
        catch (Exception e) {
            throw new PinotClientException(e);
        }
    }

    @Override
    public BrokerResponse executeQuery(String brokerAddress, Request request) throws PinotClientException {
        try {
            return executeQueryAsync(brokerAddress, request).get(brokerReadTimeout, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {
            throw new PinotClientException(e);
        }
    }

    @Override
    public CompletableFuture<BrokerResponse> executeQueryAsync(String brokerAddress, Request request) throws PinotClientException {
        return executeQueryAsync(brokerAddress, request.getQuery());
    }

    public void close() throws PinotClientException {
        client.close();
    }

}