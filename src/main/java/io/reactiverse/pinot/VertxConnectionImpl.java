/*
 *  Copyright 2021 The original authors
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
import org.apache.pinot.client.PinotClientException;
import org.apache.pinot.client.ResultSetGroup;

import io.vertx.core.Vertx;

import java.util.concurrent.ExecutionException;

public class VertxConnectionImpl implements VertxConnection {
    private final org.apache.pinot.client.Connection pinotConnection;
    private final Vertx vertx;

    VertxConnectionImpl(Vertx vertx, org.apache.pinot.client.Connection pinotConnection) {
        this.vertx = vertx;
        this.pinotConnection = pinotConnection;
    }

    @Override
    public Future<ResultSetGroup> execute(String query) {
        java.util.concurrent.Future<ResultSetGroup> originalFuture = pinotConnection.executeAsync(query);
        return vertx.executeBlocking(promise -> {
            try {
                ResultSetGroup resultSetGroup = originalFuture.get();
                promise.complete(resultSetGroup);
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }, false);
    }

    public void close() throws PinotClientException {
        pinotConnection.close();
    }

}
