import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class TestServeur {
    public static void main(String[] args) throws Exception {
        HttpServer serveur = HttpServer.create(new InetSocketAddress(8080), 0);

        // Endpoint /test (connexion)
        serveur.createContext("/test", new HttpHandler() {
            @Override
            public void handle(HttpExchange e) throws java.io.IOException {
                e.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                String reponse = "Bravo, la connexion Java Angular fonctionne !";
                byte[] bytes = reponse.getBytes(StandardCharsets.UTF_8);
                e.sendResponseHeaders(200, bytes.length);
                OutputStream os = e.getResponseBody();
                os.write(bytes);
                os.close();
            }
        });

        // Endpoint /api/personnages
        serveur.createContext("/api/personnages", new HttpHandler() {
            @Override
            public void handle(HttpExchange e) throws java.io.IOException {
                e.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                // personnage
                String json = "["
                        + "{\"id\":1,\"nom\":\"Baggins\",\"prenom\":\"Frodon\",\"description\":\"Porteur de l'Anneau\"},"
                        + "{\"id\":2,\"nom\":\"Le Gris\",\"prenom\":\"Gandalf\",\"description\":\"Magicien Istari\"},"
                        + "{\"id\":3,\"nom\":\"Skywalker\",\"prenom\":\"Luke\",\"description\":\"Jedi\"}"
                        + "]";

                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                e.sendResponseHeaders(200, bytes.length);
                OutputStream os = e.getResponseBody();
                os.write(bytes);
                os.close();
            }
        });

        serveur.setExecutor(null);
        serveur.start();
    }
}