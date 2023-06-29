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

import org.apache.pinot.client.ResultSetGroup;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

@VertxGen
public interface VertxPreparedStatement {

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<ResultSetGroup> execute();

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    void execute(Handler<AsyncResult<ResultSetGroup>> handler);

    VertxPreparedStatement setString(int parameterIndex, String value);

    VertxPreparedStatement setInt(int parameterIndex, int value);

    VertxPreparedStatement setLong(int parameterIndex, long value);

    VertxPreparedStatement setFloat(int parameterIndex, float value);

    VertxPreparedStatement setDouble(int parameterIndex, double value);
}
