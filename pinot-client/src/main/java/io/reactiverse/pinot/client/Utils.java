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

import java.util.concurrent.ExecutionException;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

class Utils {
    static <T> Future<T> transformFuture(Vertx vertx, java.util.concurrent.Future<T> originalFuture) {
        return vertx.executeBlocking(promise -> {
            try {
                T result = originalFuture.get();
                promise.complete(result);
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }, false);
    }
}
