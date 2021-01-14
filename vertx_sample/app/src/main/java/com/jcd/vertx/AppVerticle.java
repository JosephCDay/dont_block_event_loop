package com.jcd.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

public class AppVerticle extends AbstractVerticle {

    private HttpServer server;

    @Override
    public void start(Promise<Void> promise) {
        OpenAPI3RouterFactory.create(this.vertx, "src/main/resources/api.yaml", ar -> {
            if (ar.succeeded()) {
                OpenAPI3RouterFactory routerFactory = ar.result();
                // Event-loop only
                routerFactory.addHandlerByOperationId("primesEventLoopGET", routingContext -> {
                    JsonObject json = new JsonObject();
                    JsonArray arr = somethingSlow();
                    json.put("primes", arr);
                    routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .setStatusCode(200)
                            .end(json.encode());
                });

                // Multi-threaded
                routerFactory.addHandlerByOperationId("primesThreadedGET", routingContext -> {
                    JsonObject json = new JsonObject();
                    vertx.executeBlocking(hndlr -> {
                        JsonArray arr = somethingSlow();
                        json.put("primes", arr);
                        routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                                .setStatusCode(200)
                                .end(json.encode());
                    });
                });
                Router router = routerFactory.getRouter();
                server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true).setPort(8080));
                server.requestHandler(router).listen();
                promise.complete();
            } else {
                System.out.println(ar.cause());
                promise.fail("No API");
            }
        });
    }

    public JsonArray somethingSlow() {
        long end = System.currentTimeMillis() + 10000L;

        JsonArray retData = new JsonArray();
        long i = 2;
        while (System.currentTimeMillis() < end) {
            // i has not been marked -- it is prime
            boolean prime = true;
            for (long j = 2; j < i; j++) {
                if (i % j == 0) {
                    prime = false;
                    break;
                }
                if (System.currentTimeMillis() > end) {
                    return retData;
                }
            }
            if (prime) {
                retData.add(i);
            }
            i++;
        }
        return retData;
    }
}
