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

import org.apache.pinot.client.PreparedStatement;
import org.apache.pinot.client.ResultSetGroup;

import io.reactiverse.pinot.client.VertxPreparedStatement;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class VertxPreparedStatementImpl implements VertxPreparedStatement {

    private final Vertx vertx;
    private final PreparedStatement preparedStatement;

    public VertxPreparedStatementImpl(Vertx vertx, PreparedStatement preparedStatement) {
        this.vertx = vertx;
        this.preparedStatement = preparedStatement;
    }

    @Override
    public Future<ResultSetGroup> execute() {
        var originalFuture = preparedStatement.executeAsync();
        return Utils.transformFuture(vertx, originalFuture);
    }

    @Override
    public void execute(Handler<AsyncResult<ResultSetGroup>> handler) {
        execute().onComplete(handler);
    }

    @Override
    public VertxPreparedStatementImpl setString(int parameterIndex, String value) {
        preparedStatement.setString(parameterIndex, value);
        return this;
    }

    @Override
    public VertxPreparedStatementImpl setInt(int parameterIndex, int value) {
        preparedStatement.setInt(parameterIndex, value);
        return this;
    }

    @Override
    public VertxPreparedStatementImpl setLong(int parameterIndex, long value) {
        preparedStatement.setLong(parameterIndex, value);
        return this;
    }

    @Override
    public VertxPreparedStatementImpl setFloat(int parameterIndex, float value) {
        preparedStatement.setFloat(parameterIndex, value);
        return this;
    }

    @Override
    public VertxPreparedStatementImpl setDouble(int parameterIndex, double value) {
        preparedStatement.setDouble(parameterIndex, value);
        return this;
    }
}
