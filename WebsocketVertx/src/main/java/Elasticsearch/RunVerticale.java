package Elasticsearch;
import io.vertx.core.Vertx;

public class RunVerticale {
    public static void main(String[] args) {
        System.out.println("main start...");
        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MyVerticale());
    }
}
