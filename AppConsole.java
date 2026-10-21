import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import Elements.Personnage;
import Temporalite.Date;
import VueGlobale.Bibliotheque;
import VueGlobale.Livre;
import VueGlobale.Univers;

/**
 * Version console de Forge ton histoire : on utilise le meme modele et le meme
 * fichier bibliotheque.json que le serveur, mais sans passer par le web.
 */
public class AppConsole {

    static final Gson gson = new Gson();
    static final Path FICHIER = Paths.get("bibliotheque.json");
    static final Scanner entree = new Scanner(System.in);
    static Bibliotheque biblio = new Bibliotheque(1, "Ma Bibliotheque", "Principale");

    public static void main(String[] args) {
        charger();
        System.out.println("=== Forge ton histoire ===");

        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            String choix = lireTexte("Votre choix");
            if (choix == null) break; // fin de l'entree (Ctrl+D)
            switch (choix) {
                case "1": listerLivres(); break;
                case "2": creerLivre(); break;
                case "3": listerUnivers(); break;
                case "4": creerUnivers(); break;
                case "5": listerPersonnages(); break;
                case "6": creerPersonnage(); break;
                case "7": personnageVivant(); break;
                case "0": continuer = false; break;
                default:  System.out.println("Choix inconnu.");
            }
        }
        System.out.println("A bientot !");
    }

    // ================== MENU ==================

    static void afficherMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Lister les livres");
        System.out.println("2. Creer un livre");
        System.out.println("3. Lister les univers d'un livre");
        System.out.println("4. Creer un univers dans un livre");
        System.out.println("5. Lister les personnages d'un univers");
        System.out.println("6. Ajouter un personnage a un univers");
        System.out.println("7. Un personnage est-il vivant a une annee donnee ?");
        System.out.println("0. Quitter");
    }

    // ================== ACTIONS ==================

    static void listerLivres() {
        if (biblio.livreliste.isEmpty()) {
            System.out.println("Aucun livre pour le moment.");
            return;
        }
        for (Livre l : biblio.livreliste) {
            System.out.println("[" + l.id + "] " + l.nom + " (" + l.type + ") - " + l.universliste.size() + " univers");
        }
    }

    static void creerLivre() {
        String nom = lireTexte("Titre du livre");
        if (nom == null || nom.isBlank()) { System.out.println("Titre obligatoire."); return; }
        String type = lireTexte("Genre (Fantastique, Science-Fiction...)");
        String description = lireTexte("Description (facultatif)");
        Livre livre = new Livre(prochainIdLivre(), nom, valeur(type), valeur(description));
        biblio.ajoutelivre(livre);
        sauvegarder();
        System.out.println("Livre cree avec l'id " + livre.id + ".");
    }

    static void listerUnivers() {
        Livre livre = choisirLivre();
        if (livre == null) return;
        if (livre.universliste.isEmpty()) {
            System.out.println("Ce livre n'a pas encore d'univers.");
            return;
        }
        for (Univers u : livre.universliste) {
            System.out.println("[" + u.id + "] " + u.nom + " - " + u.listePersonnages.size() + " personnage(s)");
        }
    }

    static void creerUnivers() {
        Livre livre = choisirLivre();
        if (livre == null) return;
        String nom = lireTexte("Nom de l'univers");
        if (nom == null || nom.isBlank()) { System.out.println("Nom obligatoire."); return; }
        Univers univers = new Univers(prochainIdUnivers(), nom);
        univers.type = valeur(lireTexte("Type (facultatif)"));
        livre.universliste.add(univers);
        sauvegarder();
        System.out.println("Univers cree avec l'id " + univers.id + ".");
    }

    static void listerPersonnages() {
        Univers univers = choisirUnivers();
        if (univers == null) return;
        if (univers.listePersonnages.isEmpty()) {
            System.out.println("Aucun personnage dans cet univers.");
            return;
        }
        for (Personnage p : univers.listePersonnages) {
            System.out.println("[" + p.id + "] " + p.prenom + " " + p.nom + " - " + p.description
                    + "  (naissance : " + formater(p.dateNaissance) + ", deces : " + formater(p.dateDeces) + ")");
        }
    }

    static void creerPersonnage() {
        Univers univers = choisirUnivers();
        if (univers == null) return;
        String prenom = lireTexte("Prenom");
        String nom = lireTexte("Nom");
        if (prenom == null || nom == null || (prenom.isBlank() && nom.isBlank())) {
            System.out.println("Il faut au moins un prenom ou un nom.");
            return;
        }
        String description = lireTexte("Description (facultatif)");
        Personnage p = new Personnage(prochainIdPersonnage(), nom, prenom, valeur(description));

        System.out.println("Date de naissance (laisser l'annee vide pour ignorer)");
        p.dateNaissance = lireDate();
        System.out.println("Date de deces (laisser l'annee vide pour ignorer)");
        p.dateDeces = lireDate();

        // meme controle de coherence que l'API
        if (p.dateNaissance != null && p.dateDeces != null && !p.estcequevivant(p.dateNaissance)) {
            System.out.println("Incoherence : le deces ne peut pas etre avant la naissance. Personnage non ajoute.");
            return;
        }
        univers.ajoutepersonnage(p);
        sauvegarder();
        System.out.println("Personnage cree avec l'id " + p.id + ".");
    }

    static void personnageVivant() {
        Univers univers = choisirUnivers();
        if (univers == null) return;
        Integer id = lireEntier("Id du personnage");
        if (id == null) return;
        Personnage cible = null;
        for (Personnage p : univers.listePersonnages) if (p.id == id) cible = p;
        if (cible == null) { System.out.println("Personnage introuvable."); return; }
        Integer annee = lireEntier("Annee");
        if (annee == null) return;
        boolean vivant = cible.estcequevivant(new Date(annee, 1, 1));
        System.out.println(cible.prenom + " " + cible.nom + (vivant ? " est vivant(e) " : " est mort(e) ") + "en " + annee + ".");
    }

    // ================== SELECTION ==================

    static Livre choisirLivre() {
        if (biblio.livreliste.isEmpty()) { System.out.println("Creez d'abord un livre."); return null; }
        listerLivres();
        Integer id = lireEntier("Id du livre");
        if (id == null) return null;
        for (Livre l : biblio.livreliste) if (l.id == id) return l;
        System.out.println("Livre introuvable.");
        return null;
    }

    static Univers choisirUnivers() {
        Livre livre = choisirLivre();
        if (livre == null) return null;
        if (livre.universliste.isEmpty()) { System.out.println("Ce livre n'a pas encore d'univers."); return null; }
        for (Univers u : livre.universliste) System.out.println("[" + u.id + "] " + u.nom);
        Integer id = lireEntier("Id de l'univers");
        if (id == null) return null;
        for (Univers u : livre.universliste) if (u.id == id) return u;
        System.out.println("Univers introuvable.");
        return null;
    }

    // ================== SAISIE ==================

    /** Lit une ligne ; renvoie null si l'entree est terminee. */
    static String lireTexte(String question) {
        System.out.print(question + " : ");
        if (!entree.hasNextLine()) return null;
        return entree.nextLine().trim();
    }

    /** Lit un entier ; renvoie null (avec un message) si ce n'en est pas un. */
    static Integer lireEntier(String question) {
        String texte = lireTexte(question);
        if (texte == null) return null;
        try {
            return Integer.parseInt(texte);
        } catch (NumberFormatException e) {
            System.out.println("Ce n'est pas un nombre.");
            return null;
        }
    }

    /** Lit annee / mois / jour ; renvoie null si l'annee est laissee vide ou invalide. */
    static Date lireDate() {
        String a = lireTexte("  Annee");
        if (a == null || a.isBlank()) return null;
        try {
            int annee = Integer.parseInt(a);
            String m = lireTexte("  Mois (1 par defaut)");
            String j = lireTexte("  Jour (1 par defaut)");
            int mois = (m == null || m.isBlank()) ? 1 : Integer.parseInt(m);
            int jour = (j == null || j.isBlank()) ? 1 : Integer.parseInt(j);
            return new Date(annee, mois, jour);
        } catch (NumberFormatException e) {
            System.out.println("Date invalide, ignoree.");
            return null;
        }
    }

    static String valeur(String s) { return s == null ? "" : s; }

    static String formater(Date d) {
        if (d == null) return "?";
        return String.format("%02d/%02d/%d", d.tabdate.get(2).c, d.tabdate.get(1).c, d.tabdate.get(0).c);
    }

    // ================== IDENTIFIANTS ==================

    static int prochainIdLivre() {
        int max = 0;
        for (Livre l : biblio.livreliste) max = Math.max(max, l.id);
        return max + 1;
    }

    static int prochainIdUnivers() {
        int max = 0;
        for (Livre l : biblio.livreliste) for (Univers u : l.universliste) max = Math.max(max, u.id);
        return max + 1;
    }

    static int prochainIdPersonnage() {
        int max = 0;
        for (Livre l : biblio.livreliste)
            for (Univers u : l.universliste)
                for (Personnage p : u.listePersonnages) max = Math.max(max, p.id);
        return max + 1;
    }

    // ================== SAUVEGARDE ==================

    static void charger() {
        if (!Files.exists(FICHIER)) return;
        try {
            Bibliotheque chargee = gson.fromJson(Files.readString(FICHIER), Bibliotheque.class);
            if (chargee != null) biblio = chargee;
            System.out.println(biblio.livreliste.size() + " livre(s) charge(s) depuis " + FICHIER);
        } catch (Exception e) {
            System.out.println("Erreur de lecture : " + e.getMessage());
        }
    }

    static void sauvegarder() {
        try {
            Files.writeString(FICHIER, gson.toJson(biblio));
        } catch (Exception e) {
            System.out.println("Erreur d'ecriture : " + e.getMessage());
        }
    }
}
