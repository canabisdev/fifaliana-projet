package Elasticsearch;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.Router;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Map;

public class MyVerticale extends AbstractVerticle {

    Router router = Router.router(vertx);

    @Override
    public void start(Future<Void> future) throws Exception {
        HttpServer server = vertx.createHttpServer();
        server.websocketHandler(wsh -> {

            final String id = wsh.textHandlerID();
            final LocalMap<String, String> sharedData = vertx.sharedData().getLocalMap("socketbus");
            sharedData.put(id, "fhgf");

            JsonObject message = new JsonObject(getMessage());

            vertx.setPeriodic(6000, hdl -> {
                pushToClient(vertx,  message);
                System.out.println("Data envoyer ");
            });

        }).requestHandler(router).listen(8081, result -> {
            if(result.succeeded()){
                System.out.println("Server start at localhost:" );
                future.complete();
            } else {
                future.fail(result.cause());
            }
        });
    }
    public void pushToClient(Vertx vertx, JsonObject message) {
        final LocalMap<String, JsonObject> sharedData = vertx.sharedData().getLocalMap("socketbus");
        for(Map.Entry<String, JsonObject> entry : sharedData.entrySet()){
            vertx.eventBus().send(entry.getKey(), message.encode());
        }
    }
    private String getMessage() {
        String jsonResponse = "{}";
        try{
            RestClient restClient = RestClient.builder(
                    new HttpHost("localhost", 9200, "http")).build();


            Request scriptRequest = new Request("GET", "checkeligibility/_search?pretty=true");
            scriptRequest.setJsonEntity(
                    "{\n" +
                            "  \"query\": {\n" +
                            "    \"match_all\": {}\n" +
                            "  }\n" +
                            "}");

            Response scriptResponse = restClient.performRequest(scriptRequest);
            jsonResponse = EntityUtils.toString(scriptResponse.getEntity());

          //  System.out.println(jsonResponse);
            restClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }
}
