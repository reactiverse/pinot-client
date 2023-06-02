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

import org.apache.pinot.client.ResultSetGroup;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface VertxConnection {
    VertxPreparedStatement prepareStatement(String query);

    Future<ResultSetGroup> execute(String query);

    Future<ResultSetGroup> execute(@Nullable String tableName, String query);

    void execute(String query, Handler<ResultSetGroup> handler);

    void execute(@Nullable String tableName, String query, Handler<ResultSetGroup> handler);

    void close();
}
