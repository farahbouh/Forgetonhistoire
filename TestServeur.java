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
import java.util.List;
import java.util.ArrayList;

import VueGlobale.Bibliotheque;
import VueGlobale.Livre;
import VueGlobale.Univers;
import Elements.Personnage;

public class TestServeur {

    static final Gson gson = new Gson();
    static final Path FICHIER_BIBLIOTHEQUE = Paths.get("bibliotheque.json");
    static Bibliotheque biblio = new Bibliotheque(1, "Ma Bibliotheque", "Principale");
    static int prochainId = 1;

    public static void main(String[] args) throws Exception {
        chargerBibliothequeDepuisFichier();
        ajouterDonneesDeTestSiVide();

        HttpServer serveur = HttpServer.create(new InetSocketAddress(8080), 0);

        // ============ /test ============
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

        // ============ /api/personnages?livreId=X ============
        serveur.createContext("/api/personnages", new HttpHandler() {
            @Override
            public void handle(HttpExchange e) throws java.io.IOException {
                e.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                String query = e.getRequestURI().getQuery();
                String livreIdStr = null;

                if (query != null && query.startsWith("livreId=")) {
                    livreIdStr = query.substring("livreId=".length());
                }

                if (livreIdStr == null) {
                    String erreur = "{\"erreur\":\"Parametre livreId manquant\"}";
                    byte[] bytes = erreur.getBytes(StandardCharsets.UTF_8);
                    e.sendResponseHeaders(400, bytes.length);
                    OutputStream os = e.getResponseBody();
                    os.write(bytes);
                    os.close();
                    return;
                }

                int livreId = Integer.parseInt(livreIdStr);
                Livre livreTrouve = null;
                for (Livre l : biblio.livreliste) {
                    if (l.id == livreId) {
                        livreTrouve = l;
                        break;
                    }
                }

                if (livreTrouve == null) {
                    String erreur = "{\"erreur\":\"Livre introuvable\"}";
                    byte[] bytes = erreur.getBytes(StandardCharsets.UTF_8);
                    e.sendResponseHeaders(404, bytes.length);
                    OutputStream os = e.getResponseBody();
                    os.write(bytes);
                    os.close();
                    return;
                }

                List<Personnage> tousLesPersonnages = new ArrayList<>();
                for (Univers u : livreTrouve.universliste) {
                    tousLesPersonnages.addAll(u.listePersonnages);
                }

                String json = gson.toJson(tousLesPersonnages);
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                e.sendResponseHeaders(200, bytes.length);
                OutputStream os = e.getResponseBody();
                os.write(bytes);
                os.close();
            }
        });

        // ============ /api/univers?livreId=X ============
        serveur.createContext("/api/univers", new HttpHandler() {
            @Override
            public void handle(HttpExchange e) throws java.io.IOException {
                String query = e.getRequestURI().getQuery();
                String livreIdStr = null;
                if (query != null && query.startsWith("livreId=")) {
                    livreIdStr = query.substring("livreId=".length());
                }

                if (livreIdStr == null) {
                    e.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    String erreur = "{\"erreur\":\"Parametre livreId manquant\"}";
                    byte[] bytes = erreur.getBytes(StandardCharsets.UTF_8);
                    e.sendResponseHeaders(400, bytes.length);
                    OutputStream os = e.getResponseBody();
                    os.write(bytes);
                    os.close();
                    return;
                }

                gererUnivers(e, Integer.parseInt(livreIdStr));
            }
        });

        // ============ /api/livres ============
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

    // ============ Route POST/GET : univers d'un livre ============
    static void gererUnivers(HttpExchange e, int livreId) throws java.io.IOException {
        e.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        e.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        e.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        String methode = e.getRequestMethod();

        if (methode.equals("OPTIONS")) {
            e.sendResponseHeaders(204, -1);
            return;
        }

        // Trouver le livre
        Livre livreTrouve = null;
        for (Livre l : biblio.livreliste) {
            if (l.id == livreId) {
                livreTrouve = l;
                break;
            }
        }

        if (livreTrouve == null) {
            String erreur = "{\"erreur\":\"Livre introuvable\"}";
            byte[] bytes = erreur.getBytes(StandardCharsets.UTF_8);
            e.sendResponseHeaders(404, bytes.length);
            OutputStream os = e.getResponseBody();
            os.write(bytes);
            os.close();
            return;
        }

        // ===== POST : créer un univers =====
        if (methode.equals("POST")) {
            InputStream is = e.getRequestBody();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int lus;
            while ((lus = is.read(data)) != -1) {
                buffer.write(data, 0, lus);
            }
            String corps = buffer.toString(StandardCharsets.UTF_8);

            UniversEntrant entrant = gson.fromJson(corps, UniversEntrant.class);
            String nom = entrant.nom != null ? entrant.nom : "Univers sans nom";
            String type = entrant.type != null ? entrant.type : "";

            int nouvelId = 1;
            for (Livre l : biblio.livreliste) {
                for (Univers u : l.universliste) {
                    if (u.id >= nouvelId) nouvelId = u.id + 1;
                }
            }

            Univers nouveau = new Univers(nouvelId, nom);
            nouveau.type = type;
            livreTrouve.universliste.add(nouveau);
            sauvegarderBibliothequeDansFichier();

            String reponse = "{\"statut\":\"ok\",\"id\":" + nouveau.id + "}";
            byte[] bytes = reponse.getBytes(StandardCharsets.UTF_8);
            e.sendResponseHeaders(200, bytes.length);
            OutputStream os = e.getResponseBody();
            os.write(bytes);
            os.close();
            return;
        }

        // ===== GET : lister les univers =====
        String json = gson.toJson(livreTrouve.universliste);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        e.sendResponseHeaders(200, bytes.length);
        OutputStream os = e.getResponseBody();
        os.write(bytes);
        os.close();
    }

    // ============ Classes pour lire le JSON entrant ============
    static class LivreEntrant {
        String nom;
        String type;
        String description;
    }

    static class UniversEntrant {
        String nom;
        String type;
    }

    // ============ Données de test ============
    static void ajouterDonneesDeTestSiVide() {
        if (!biblio.livreliste.isEmpty()) {
            System.out.println("Bibliotheque non vide, pas de donnees de test ajoutees");
            return;
        }

        System.out.println("Bibliotheque vide, ajout de donnees de test...");

        // --- Livre 1 : Le Seigneur des Anneaux ---
        Livre livre1 = new Livre(1, "Le Seigneur des Anneaux", "Fantastique", "Une quete epique");
        Univers univers1 = new Univers(1, "Terre du Milieu");

        Personnage frodon = new Personnage(1, "Baggins", "Frodon", "Porteur de l'Anneau");
        Personnage gandalf = new Personnage(2, "Le Gris", "Gandalf", "Magicien Istari");

        univers1.ajoutepersonnage(frodon);
        univers1.ajoutepersonnage(gandalf);
        livre1.universliste.add(univers1);

        // --- Livre 2 : Star Wars ---
        Livre livre2 = new Livre(2, "Star Wars", "Science-Fiction", "Une galaxie lointaine");
        Univers univers2 = new Univers(2, "Galaxie lointaine");

        Personnage luke = new Personnage(3, "Skywalker", "Luke", "Jedi");
        Personnage leia = new Personnage(4, "Organa", "Leia", "Princesse rebelle");

        univers2.ajoutepersonnage(luke);
        univers2.ajoutepersonnage(leia);
        livre2.universliste.add(univers2);

        // --- Livre 3 : avec DEUX univers (univers parallèles) ---
        Livre livre3 = new Livre(3, "Chroniques Paralleles", "Fantastique", "Deux mondes en parallele");
        Univers univers3a = new Univers(3, "Monde Reel");
        Univers univers3b = new Univers(4, "Monde Miroir");

        Personnage alice = new Personnage(5, "Dupont", "Alice", "Vit dans le monde reel");
        Personnage aliceMiroir = new Personnage(6, "Dupont", "Alice (miroir)", "Son double dans le monde miroir");

        univers3a.ajoutepersonnage(alice);
        univers3b.ajoutepersonnage(aliceMiroir);
        livre3.universliste.add(univers3a);
        livre3.universliste.add(univers3b);

        biblio.ajoutelivre(livre1);
        biblio.ajoutelivre(livre2);
        biblio.ajoutelivre(livre3);

        prochainId = 4;

        sauvegarderBibliothequeDansFichier();
        System.out.println("3 livres de test ajoutes avec leurs personnages");
    }

    // ============ Chargement / sauvegarde ============
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