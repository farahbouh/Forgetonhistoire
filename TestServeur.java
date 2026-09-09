import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import VueGlobale.Bibliotheque;
import VueGlobale.Livre;

public class TestServeur {

    static final Gson gson = new Gson();
    static final Path FICHIER_BIBLIOTHEQUE = Paths.get("bibliotheque.json");
    static Bibliotheque biblio = new Bibliotheque(1, "Ma Bibliotheque", "Principale");
    static int prochainId = 1;

    public static void main(String[] args) throws Exception {
        chargerBibliothequeDepuisFichier();

        HttpServer serveur = HttpServer.create(new InetSocketAddress(8080), 0);

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

        serveur.createContext("/api/personnages", new HttpHandler() {
            @Override
            public void handle(HttpExchange e) throws java.io.IOException {
                e.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
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

        // Endpoint /api/livres : GET (lister) et POST (creer)
        serveur.createContext("/api/livres", new HttpHandler() {
            @Override
            public void handle(HttpExchange e) throws java.io.IOException {
                e.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                e.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                e.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

                String methode = e.getRequestMethod();

                if (methode.equals("OPTIONS")) {
                    e.sendResponseHeaders(204, -1);
                    return;
                }

                if (methode.equals("POST")) {
                    InputStream is = e.getRequestBody();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int lus;
                    while ((lus = is.read(data)) != -1) {
                        buffer.write(data, 0, lus);
                    }
                    String corps = buffer.toString(StandardCharsets.UTF_8);

                    // On lit juste les champs recus (nom, type, description) via une classe temporaire
                    LivreEntrant entrant = gson.fromJson(corps, LivreEntrant.class);
                    String nom = entrant.nom != null ? entrant.nom : "Sans nom";
                    String type = entrant.type != null ? entrant.type : "";
                    String description = entrant.description != null ? entrant.description : "";

                    Livre nouveau = new Livre(prochainId, nom, type, description);
                    prochainId++;
                    biblio.ajoutelivre(nouveau);
                    sauvegarderBibliothequeDansFichier();

                    String reponse = "{\"statut\":\"ok\",\"id\":" + nouveau.id + "}";
                    byte[] bytes = reponse.getBytes(StandardCharsets.UTF_8);
                    e.sendResponseHeaders(200, bytes.length);
                    OutputStream os = e.getResponseBody();
                    os.write(bytes);
                    os.close();
                    return;
                }

                // GET : renvoyer tous les livres de la bibliotheque
                String json = gson.toJson(biblio.livreliste);
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
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

    // Petite classe utilisee uniquement pour lire le JSON envoye par Angular (nom/type/description)
    static class LivreEntrant {
        String nom;
        String type;
        String description;
    }

    static void chargerBibliothequeDepuisFichier() {
        if (Files.exists(FICHIER_BIBLIOTHEQUE)) {
            try {
                String contenu = Files.readString(FICHIER_BIBLIOTHEQUE);
                Bibliotheque chargee = gson.fromJson(contenu, Bibliotheque.class);
                if (chargee != null) {
                    biblio = chargee;
                    for (Livre l : biblio.livreliste) {
                        if (l.id >= prochainId) prochainId = l.id + 1;
                    }
                }
                System.out.println(biblio.livreliste.size() + " livre(s) charge(s) depuis " + FICHIER_BIBLIOTHEQUE);
            } catch (Exception ex) {
                System.out.println("Erreur de lecture de la bibliotheque: " + ex.getMessage());
            }
        }
    }

    static void sauvegarderBibliothequeDansFichier() {
        try {
            String json = gson.toJson(biblio);
            Files.writeString(FICHIER_BIBLIOTHEQUE, json);
        } catch (Exception ex) {
            System.out.println("Erreur d'ecriture de la bibliotheque: " + ex.getMessage());
        }
    }
}