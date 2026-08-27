import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class TestServeur {
    public static void main(String[] args) throws Exception {
        HttpServer serveur = HttpServer.create(new InetSocketAddress(8080), 0);

        serveur.createContext("/test", new HttpHandler() {
            @Override
            public void handle(HttpExchange e) throws java.io.IOException {
                String reponse = "Bravo, la connexion Java Angular fonctionne !";
                byte[] bytes = reponse.getBytes(StandardCharsets.UTF_8);
                e.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                e.sendResponseHeaders(200, bytes.length);
                OutputStream os = e.getResponseBody();
                os.write(bytes);
                os.close();
            }
        });

        serveur.setExecutor(null);
        serveur.start();
        System.out.println("Serveur Java allume sur http://localhost:8080");
        System.out.println("Appuie sur Ctrl+C pour l'arreter");
    }
}