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
package io.reactiverse.pinot.client.impl;

import org.apache.pinot.client.PinotClientException;
import org.apache.pinot.client.ResultSetGroup;

import io.reactiverse.pinot.client.VertxConnection;
import io.reactiverse.pinot.client.VertxPreparedStatement;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class VertxConnectionImpl implements VertxConnection {
    private final org.apache.pinot.client.Connection pinotConnection;
    private final Vertx vertx;

    public VertxConnectionImpl(Vertx vertx, org.apache.pinot.client.Connection pinotConnection) {
        this.vertx = vertx;
        this.pinotConnection = pinotConnection;
    }

    @Override
    public VertxPreparedStatement prepareStatement(String query) {
        var originalPreparedStatement = pinotConnection.prepareStatement(query);
        return new VertxPreparedStatementImpl(vertx, originalPreparedStatement);
    }

    @Override
    public Future<ResultSetGroup> execute(String query) {
        var originalFuture = pinotConnection.executeAsync(query);
        return Future.fromCompletionStage(originalFuture);
    }

    @Override
    public Future<ResultSetGroup> execute(@Nullable String tableName, String query) {
        var originalFuture = pinotConnection.executeAsync(tableName, query);
        return Future.fromCompletionStage(originalFuture);
    }

    @Override
    public void execute(String query, Handler<AsyncResult<ResultSetGroup>> handler) {
        execute(query).onComplete(handler);
    }

    @Override
    public void execute(@Nullable String tableName, String query, Handler<AsyncResult<ResultSetGroup>> handler) {
        execute(tableName, query).onComplete(handler);
    }

    public void close() throws PinotClientException {
        pinotConnection.close();
    }

}
