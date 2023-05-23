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

import org.apache.pinot.client.PinotClientException;
import org.apache.pinot.client.ResultSetGroup;

import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class VertxConnectionImpl implements VertxConnection {
    private final org.apache.pinot.client.Connection pinotConnection;
    private final Vertx vertx;

    VertxConnectionImpl(Vertx vertx, org.apache.pinot.client.Connection pinotConnection) {
        this.vertx = vertx;
        this.pinotConnection = pinotConnection;
    }

    @Override
    public void execute(String query, Handler<ResultSetGroup> handler) {
        Context context = vertx.getOrCreateContext();
        context.runOnContext(action -> {
            ResultSetGroup resultSetGroup = pinotConnection.execute(query);
            handler.handle(resultSetGroup);
        });
    }

    public void close() throws PinotClientException {
        pinotConnection.close();
    }

}
