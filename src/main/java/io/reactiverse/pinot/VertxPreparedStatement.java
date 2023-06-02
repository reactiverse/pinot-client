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

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.apache.pinot.client.PreparedStatement;
import org.apache.pinot.client.ResultSetGroup;

import static io.reactiverse.pinot.Utils.transformFuture;


public class VertxPreparedStatement {

  private final Vertx vertx;
  private final PreparedStatement preparedStatement;

  VertxPreparedStatement(Vertx vertx, PreparedStatement preparedStatement) {
    this.vertx = vertx;
    this.preparedStatement = preparedStatement;
  }


  public Future<ResultSetGroup> execute() {
    var originalFuture = preparedStatement.executeAsync();
    return transformFuture(vertx, originalFuture);
  }

  public void execute(Handler<ResultSetGroup> handler) {
    execute().onSuccess(handler);
  }

  public VertxPreparedStatement setString(int parameterIndex, String value) {
    preparedStatement.setString(parameterIndex, value);
    return this;
  }

  public VertxPreparedStatement setInt(int parameterIndex, int value) {
    preparedStatement.setInt(parameterIndex, value);
    return this;
  }

  public VertxPreparedStatement setLong(int parameterIndex, long value) {
    preparedStatement.setLong(parameterIndex, value);
    return this;
  }

  public VertxPreparedStatement setFloat(int parameterIndex, float value) {
    preparedStatement.setFloat(parameterIndex, value);
    return this;
  }

  public VertxPreparedStatement setDouble(int parameterIndex, double value) {
    preparedStatement.setDouble(parameterIndex, value);
    return this;
  }
}
