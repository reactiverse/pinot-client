package io.reactiverse.pinot;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.concurrent.ExecutionException;

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
