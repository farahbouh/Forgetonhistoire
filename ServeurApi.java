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
import java.util.ArrayList;

import VueGlobale.Bibliotheque;
import VueGlobale.Livre;
import VueGlobale.Univers;
import Elements.Personnage;
import Elements.Lieux;
import Elements.Evenement;
import Temporalite.Date;
import Temporalite.Calendrier;

public class ServeurApi {

    // ============ ETAT GLOBAL ============
    static final Gson gson = new Gson();
    static final Path FICHIER_BIBLIOTHEQUE = Paths.get("bibliotheque.json");
    static Bibliotheque biblio = new Bibliotheque(1, "Ma Bibliotheque", "Principale");
    static int prochainId = 1;

    // ============ POINT D'ENTREE ============
    public static void main(String[] args) throws Exception {
        chargerBibliothequeDepuisFichier();
        ajouterDonneesDeTestSiVide();

        HttpServer serveur = HttpServer.create(new InetSocketAddress(8080), 0);

        serveur.createContext("/test", ServeurApi::handleTest);
        serveur.createContext("/api/personnages", ServeurApi::handlePersonnages);
        serveur.createContext("/api/univers", ServeurApi::handleUnivers);
        serveur.createContext("/api/livres", ServeurApi::handleLivres);
        serveur.createContext("/api/lieux", ServeurApi::handleLieux);
        serveur.createContext("/api/evenements", ServeurApi::handleEvenements);
        serveur.createContext("/api/calendrier", ServeurApi::handleCalendrier);
        // /api/liens : reporte, le modele Lien est un groupe de 3 listes (pas un lien binaire)

        serveur.setExecutor(null);
        serveur.start();
        System.out.println("Serveur Java allume sur http://localhost:8080");
    }

    // ============================================================
    //                       HANDLERS
    // ============================================================

    static void handleTest(HttpExchange e) throws java.io.IOException {
        cors(e, "GET, OPTIONS");
        if (estPreflight(e)) return;
        repondreTexte(e, 200, "Bravo, la connexion Java Angular fonctionne !");
    }

    // ---------------------- /api/personnages ----------------------
    static void handlePersonnages(HttpExchange e) throws java.io.IOException {
        cors(e, "GET, POST, PUT, OPTIONS");
        if (estPreflight(e)) return;

        Integer universId = extraireIntQuery(e, "universId");
        if (universId == null) {
            repondreErreur(e, 400, "Parametre universId manquant");
            return;
        }

        Univers univers = trouverUnivers(universId);
        if (univers == null) {
            repondreErreur(e, 404, "Univers introuvable");
            return;
        }

        switch (e.getRequestMethod()) {
            case "POST":  creerPersonnage(e, univers);     return;
            case "PUT":   modifierPersonnage(e, univers);  return;
            case "GET":   repondreJson(e, 200, univers.listePersonnages); return;
            default:      repondreErreur(e, 405, "Methode non supportee");
        }
    }

    static void creerPersonnage(HttpExchange e, Univers univers) throws java.io.IOException {
        PersonnageEntrant entrant = lireCorps(e, PersonnageEntrant.class);
        Personnage nouveau = new Personnage(genererIdPersonnage(), entrant.nom, entrant.prenom, entrant.description);

        appliquerDates(nouveau, entrant);

        if (!datesCoherentes(nouveau)) {
            repondreErreur(e, 400, "Incoherence : le deces ne peut pas etre avant la naissance");
            return;
        }

        univers.listePersonnages.add(nouveau);
        sauvegarderBibliothequeDansFichier();
        repondreJson(e, 200, new ReponseId("ok", nouveau.id));
    }

    static void modifierPersonnage(HttpExchange e, Univers univers) throws java.io.IOException {
        PersonnageEntrant entrant = lireCorps(e, PersonnageEntrant.class);

        if (entrant.id == null) {
            repondreErreur(e, 400, "ID manquant pour la modification");
            return;
        }

        Personnage cible = trouverPersonnage(univers, entrant.id);
        if (cible == null) {
            repondreErreur(e, 404, "Personnage introuvable");
            return;
        }

        if (entrant.nom != null)         cible.nom = entrant.nom;
        if (entrant.prenom != null)      cible.prenom = entrant.prenom;
        if (entrant.description != null) cible.description = entrant.description;

        appliquerDates(cible, entrant);

        if (!datesCoherentes(cible)) {
            repondreErreur(e, 400, "Incoherence : le deces ne peut pas etre avant la naissance");
            return;
        }

        sauvegarderBibliothequeDansFichier();
        repondreJson(e, 200, new ReponseId("ok", cible.id));
    }

    // ---------------------- /api/univers ----------------------
    static void handleUnivers(HttpExchange e) throws java.io.IOException {
        cors(e, "GET, POST, OPTIONS");
        if (estPreflight(e)) return;

        Integer livreId = extraireIntQuery(e, "livreId");
        if (livreId == null) {
            repondreErreur(e, 400, "Parametre livreId manquant");
            return;
        }

        Livre livre = trouverLivre(livreId);
        if (livre == null) {
            repondreErreur(e, 404, "Livre introuvable");
            return;
        }

        switch (e.getRequestMethod()) {
            case "POST": creerUnivers(e, livre); return;
            case "GET":  repondreJson(e, 200, livre.universliste); return;
            default:     repondreErreur(e, 405, "Methode non supportee");
        }
    }

    static void creerUnivers(HttpExchange e, Livre livre) throws java.io.IOException {
        UniversEntrant entrant = lireCorps(e, UniversEntrant.class);
        Univers nouveau = new Univers(genererIdUnivers(), entrant.nom);
        nouveau.type = entrant.type != null ? entrant.type : "";

        livre.universliste.add(nouveau);
        sauvegarderBibliothequeDansFichier();
        repondreJson(e, 200, new ReponseId("ok", nouveau.id));
    }

    // ---------------------- /api/livres ----------------------
    static void handleLivres(HttpExchange e) throws java.io.IOException {
        cors(e, "GET, POST, OPTIONS");
        if (estPreflight(e)) return;

        switch (e.getRequestMethod()) {
            case "POST": creerLivre(e); return;
            case "GET":  repondreJson(e, 200, biblio.livreliste); return;
            default:     repondreErreur(e, 405, "Methode non supportee");
        }
    }

    static void creerLivre(HttpExchange e) throws java.io.IOException {
        LivreEntrant entrant = lireCorps(e, LivreEntrant.class);
        Livre nouveau = new Livre(
            prochainId++,
            entrant.nom != null ? entrant.nom : "Sans nom",
            entrant.type != null ? entrant.type : "",
            entrant.description != null ? entrant.description : ""
        );

        biblio.ajoutelivre(nouveau);
        sauvegarderBibliothequeDansFichier();
        repondreJson(e, 200, new ReponseId("ok", nouveau.id));
    }

    // ============================================================
    //                 /api/lieux
    // ============================================================
    static void handleLieux(HttpExchange e) throws java.io.IOException {
        cors(e, "GET, POST, PUT, OPTIONS");
        if (estPreflight(e)) return;

        Integer universId = extraireIntQuery(e, "universId");
        if (universId == null) {
            repondreErreur(e, 400, "Parametre universId manquant");
            return;
        }

        Univers univers = trouverUnivers(universId);
        if (univers == null) {
            repondreErreur(e, 404, "Univers introuvable");
            return;
        }

        switch (e.getRequestMethod()) {
            case "POST": creerLieu(e, univers);    return;
            case "PUT":  modifierLieu(e, univers); return;
            case "GET":  repondreJson(e, 200, univers.listeLieux); return;
            default:     repondreErreur(e, 405, "Methode non supportee");
        }
    }

    static void creerLieu(HttpExchange e, Univers univers) throws java.io.IOException {
        LieuEntrant entrant = lireCorps(e, LieuEntrant.class);
        Lieux nouveau = new Lieux(genererIdLieu(), entrant.nom, entrant.type);

        univers.listeLieux.add(nouveau);
        sauvegarderBibliothequeDansFichier();
        repondreJson(e, 200, new ReponseId("ok", nouveau.id));
    }

    static void modifierLieu(HttpExchange e, Univers univers) throws java.io.IOException {
        LieuEntrant entrant = lireCorps(e, LieuEntrant.class);

        if (entrant.id == null) {
            repondreErreur(e, 400, "ID manquant pour la modification");
            return;
        }

        Lieux cible = trouverLieu(univers, entrant.id);
        if (cible == null) {
            repondreErreur(e, 404, "Lieu introuvable");
            return;
        }

        // Le modele Lieux utilise des listes pour nom et type (un lieu peut changer
        // de nom/type au fil du temps). On ajoute ici une nouvelle entree si elle est fournie.
        if (entrant.nom != null && !entrant.nom.isBlank()) {
            cible.nom.add(entrant.nom);
        }
        if (entrant.type != null && !entrant.type.isBlank()) {
            cible.type.add(entrant.type);
        }

        sauvegarderBibliothequeDansFichier();
        repondreJson(e, 200, new ReponseId("ok", cible.id));
    }

    // ============================================================
    //                 /api/evenements
    // ============================================================
    static void handleEvenements(HttpExchange e) throws java.io.IOException {
        cors(e, "GET, POST, PUT, OPTIONS");
        if (estPreflight(e)) return;

        Integer universId = extraireIntQuery(e, "universId");
        if (universId == null) {
            repondreErreur(e, 400, "Parametre universId manquant");
            return;
        }

        Univers univers = trouverUnivers(universId);
        if (univers == null) {
            repondreErreur(e, 404, "Univers introuvable");
            return;
        }

        switch (e.getRequestMethod()) {
            case "POST": creerEvenement(e, univers);    return;
            case "PUT":  modifierEvenement(e, univers); return;
            case "GET":  repondreJson(e, 200, univers.listeEvenements); return;
            default:     repondreErreur(e, 405, "Methode non supportee");
        }
    }

    static void creerEvenement(HttpExchange e, Univers univers) throws java.io.IOException {
        EvenementEntrant entrant = lireCorps(e, EvenementEntrant.class);

        Evenement nouveau = new Evenement(
            genererIdEvenement(),
            entrant.titre != null ? entrant.titre : "",
            entrant.description != null ? entrant.description : ""
        );

        if (entrant.jour != null && entrant.mois != null && entrant.annee != null) {
            nouveau.dateDebut = new Date(entrant.annee, entrant.mois, entrant.jour);
        }

        univers.listeEvenements.add(nouveau);
        sauvegarderBibliothequeDansFichier();
        repondreJson(e, 200, new ReponseId("ok", nouveau.id));
    }

    static void modifierEvenement(HttpExchange e, Univers univers) throws java.io.IOException {
        EvenementEntrant entrant = lireCorps(e, EvenementEntrant.class);

        if (entrant.id == null) {
            repondreErreur(e, 400, "ID manquant pour la modification");
            return;
        }

        Evenement cible = trouverEvenement(univers, entrant.id);
        if (cible == null) {
            repondreErreur(e, 404, "Evenement introuvable");
            return;
        }

        if (entrant.titre != null)       cible.titre = entrant.titre;
        if (entrant.description != null) cible.description = entrant.description;
        if (entrant.jour != null && entrant.mois != null && entrant.annee != null) {
            cible.dateDebut = new Date(entrant.annee, entrant.mois, entrant.jour);
        }

        sauvegarderBibliothequeDansFichier();
        repondreJson(e, 200, new ReponseId("ok", cible.id));
    }

    // ============================================================
    //                 /api/calendrier
    // ============================================================
    static void handleCalendrier(HttpExchange e) throws java.io.IOException {
        cors(e, "GET, POST, OPTIONS");
        if (estPreflight(e)) return;

        Integer universId = extraireIntQuery(e, "universId");
        if (universId == null) {
            repondreErreur(e, 400, "Parametre universId manquant");
            return;
        }

        Univers univers = trouverUnivers(universId);
        if (univers == null) {
            repondreErreur(e, 404, "Univers introuvable");
            return;
        }

        switch (e.getRequestMethod()) {
            case "POST": ajouterDateCalendrier(e, univers); return;
            case "GET":
                if (univers.calendrier == null) {
                    repondreErreur(e, 404, "Calendrier non initialise pour cet univers");
                    return;
                }
                repondreJson(e, 200, univers.calendrier);
                return;
            default:     repondreErreur(e, 405, "Methode non supportee");
        }
    }

    static void ajouterDateCalendrier(HttpExchange e, Univers univers) throws java.io.IOException {
        CalendrierEntrant entrant = lireCorps(e, CalendrierEntrant.class);

        if (entrant.jour == null || entrant.mois == null || entrant.annee == null) {
            repondreErreur(e, 400, "Jour, mois et annee obligatoires");
            return;
        }

        Date nouvelle = new Date(entrant.annee, entrant.mois, entrant.jour);

        // Le calendrier prend (maxi, mini) dans le constructeur.
        // On l'initialise a la premiere utilisation.
        if (univers.calendrier == null) {
            univers.calendrier = new Calendrier(
                new Date(9999, 12, 31),
                new Date(-9999, 1, 1)
            );
        }

        int avant = univers.calendrier.grille.size();
        univers.calendrier.ajouterDate(nouvelle);
        int apres = univers.calendrier.grille.size();

        if (apres == avant) {
            // ajouterDate n'a rien ajoute (doublon, hors bornes, etc.)
            repondreErreur(e, 400, "Date invalide ou deja presente (ou hors bornes du calendrier)");
            return;
        }

        sauvegarderBibliothequeDansFichier();
        repondreJson(e, 200, new ReponseId("ok", 0));
    }

    // ============================================================
    //                    HELPERS HTTP
    // ============================================================

    static void cors(HttpExchange e, String methodes) {
        e.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        e.getResponseHeaders().add("Access-Control-Allow-Methods", methodes);
        e.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    static boolean estPreflight(HttpExchange e) throws java.io.IOException {
        if (e.getRequestMethod().equals("OPTIONS")) {
            e.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    static <T> T lireCorps(HttpExchange e, Class<T> classe) throws java.io.IOException {
        InputStream is = e.getRequestBody();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int lus;
        while ((lus = is.read(data)) != -1) buffer.write(data, 0, lus);
        String corps = buffer.toString(StandardCharsets.UTF_8);
        return gson.fromJson(corps, classe);
    }

    static void repondreJson(HttpExchange e, int code, Object contenu) throws java.io.IOException {
        String json = gson.toJson(contenu);
        repondreTexte(e, code, json);
    }

    static void repondreTexte(HttpExchange e, int code, String texte) throws java.io.IOException {
        byte[] bytes = texte.getBytes(StandardCharsets.UTF_8);
        e.sendResponseHeaders(code, bytes.length);
        OutputStream os = e.getResponseBody();
        os.write(bytes);
        os.close();
    }

    static void repondreErreur(HttpExchange e, int code, String message) throws java.io.IOException {
        repondreTexte(e, code, "{\"erreur\":\"" + message + "\"}");
    }

    static Integer extraireIntQuery(HttpExchange e, String nom) {
        String query = e.getRequestURI().getQuery();
        if (query == null) return null;
        for (String part : query.split("&")) {
            if (part.startsWith(nom + "=")) {
                try {
                    return Integer.parseInt(part.substring(nom.length() + 1));
                } catch (NumberFormatException ex) {
                    return null;
                }
            }
        }
        return null;
    }

    // ============================================================
    //                    HELPERS METIER
    // ============================================================

    static Univers trouverUnivers(int id) {
        for (Livre l : biblio.livreliste)
            for (Univers u : l.universliste)
                if (u.id == id) return u;
        return null;
    }

    static Livre trouverLivre(int id) {
        for (Livre l : biblio.livreliste)
            if (l.id == id) return l;
        return null;
    }

    static Personnage trouverPersonnage(Univers u, int id) {
        for (Personnage p : u.listePersonnages)
            if (p.id == id) return p;
        return null;
    }

    static Lieux trouverLieu(Univers u, int id) {
        for (Lieux l : u.listeLieux)
            if (l.id == id) return l;
        return null;
    }

    static Evenement trouverEvenement(Univers u, int id) {
        for (Evenement ev : u.listeEvenements)
            if (ev.id == id) return ev;
        return null;
    }

    // ============================================================
    //                    GENERATEURS D'ID
    // ============================================================

    static int genererIdPersonnage() {
        int max = 0;
        for (Livre l : biblio.livreliste)
            for (Univers u : l.universliste)
                for (Personnage p : u.listePersonnages)
                    if (p.id >= max) max = p.id + 1;
        return max == 0 ? 1 : max;
    }

    static int genererIdUnivers() {
        int max = 0;
        for (Livre l : biblio.livreliste)
            for (Univers u : l.universliste)
                if (u.id >= max) max = u.id + 1;
        return max == 0 ? 1 : max;
    }

    static int genererIdLieu() {
        int max = 0;
        for (Livre l : biblio.livreliste)
            for (Univers u : l.universliste)
                for (Lieux lieu : u.listeLieux)
                    if (lieu.id >= max) max = lieu.id + 1;
        return max == 0 ? 1 : max;
    }

    static int genererIdEvenement() {
        int max = 0;
        for (Livre l : biblio.livreliste)
            for (Univers u : l.universliste)
                for (Evenement ev : u.listeEvenements)
                    if (ev.id >= max) max = ev.id + 1;
        return max == 0 ? 1 : max;
    }

    static void appliquerDates(Personnage p, PersonnageEntrant e) {
        if (e.jour != null && e.mois != null && e.annee != null)
            p.dateNaissance = new Date(e.annee, e.mois, e.jour);
        if (e.jourDeces != null && e.moisDeces != null && e.anneeDeces != null)
            p.dateDeces = new Date(e.anneeDeces, e.moisDeces, e.jourDeces);
    }

    static boolean datesCoherentes(Personnage p) {
        if (p.dateNaissance == null || p.dateDeces == null) return true;
        return p.estcequevivant(p.dateNaissance);
    }

    // ============================================================
    //                    CLASSES DTO
    // ============================================================

    static class LivreEntrant {
        String nom;
        String type;
        String description;
    }

    static class UniversEntrant {
        String nom;
        String type;
    }

    static class PersonnageEntrant {
        Integer id;
        String nom;
        String prenom;
        String description;
        Integer jour;
        Integer mois;
        Integer annee;
        Integer jourDeces;
        Integer moisDeces;
        Integer anneeDeces;
    }

    static class LieuEntrant {
        Integer id;
        String nom;
        String type;
    }

    static class EvenementEntrant {
        Integer id;
        String titre;
        String description;
        Integer jour;
        Integer mois;
        Integer annee;
    }

    static class CalendrierEntrant {
        Integer jour;
        Integer mois;
        Integer annee;
    }

    static class ReponseId {
        String statut;
        int id;
        ReponseId(String statut, int id) {
            this.statut = statut;
            this.id = id;
        }
    }

    // ============================================================
    //              DONNEES DE TEST + PERSISTANCE
    // ============================================================

    static void ajouterDonneesDeTestSiVide() {
        if (!biblio.livreliste.isEmpty()) return;

        System.out.println("Bibliotheque vide, ajout de donnees de test...");

        Livre livre1 = new Livre(1, "Le Seigneur des Anneaux", "Fantastique", "Une quete epique");
        Univers univers1 = new Univers(1, "Terre du Milieu");
        univers1.ajoutepersonnage(new Personnage(1, "Baggins", "Frodon", "Porteur de l'Anneau"));
        univers1.ajoutepersonnage(new Personnage(2, "Le Gris", "Gandalf", "Magicien Istari"));
        livre1.universliste.add(univers1);

        Livre livre2 = new Livre(2, "Star Wars", "Science-Fiction", "Une galaxie lointaine");
        Univers univers2 = new Univers(2, "Galaxie lointaine");
        univers2.ajoutepersonnage(new Personnage(3, "Skywalker", "Luke", "Jedi"));
        univers2.ajoutepersonnage(new Personnage(4, "Organa", "Leia", "Princesse rebelle"));
        livre2.universliste.add(univers2);

        Livre livre3 = new Livre(3, "Chroniques Paralleles", "Fantastique", "Deux mondes en parallele");
        Univers univers3a = new Univers(3, "Monde Reel");
        Univers univers3b = new Univers(4, "Monde Miroir");
        univers3a.ajoutepersonnage(new Personnage(5, "Dupont", "Alice", "Vit dans le monde reel"));
        univers3b.ajoutepersonnage(new Personnage(6, "Dupont", "Alice (miroir)", "Son double dans le monde miroir"));
        livre3.universliste.add(univers3a);
        livre3.universliste.add(univers3b);

        biblio.ajoutelivre(livre1);
        biblio.ajoutelivre(livre2);
        biblio.ajoutelivre(livre3);

        prochainId = 4;
        sauvegarderBibliothequeDansFichier();
        System.out.println("3 livres de test ajoutes avec leurs personnages");
    }

    static void chargerBibliothequeDepuisFichier() {
        if (!Files.exists(FICHIER_BIBLIOTHEQUE)) return;
        try {
            String contenu = Files.readString(FICHIER_BIBLIOTHEQUE);
            Bibliotheque chargee = gson.fromJson(contenu, Bibliotheque.class);
            if (chargee != null) {
                biblio = chargee;
                for (Livre l : biblio.livreliste)
                    if (l.id >= prochainId) prochainId = l.id + 1;
            }
            System.out.println(biblio.livreliste.size() + " livre(s) charge(s) depuis " + FICHIER_BIBLIOTHEQUE);
        } catch (Exception ex) {
            System.out.println("Erreur de lecture de la bibliotheque: " + ex.getMessage());
        }
    }

    static void sauvegarderBibliothequeDansFichier() {
        try {
            Files.writeString(FICHIER_BIBLIOTHEQUE, gson.toJson(biblio));
        } catch (Exception ex) {
            System.out.println("Erreur d'ecriture de la bibliotheque: " + ex.getMessage());
        }
    }
}