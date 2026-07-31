import java.util.ArrayList;
import Temporalite.Calendrier;
import Temporalite.Date;
import VueGlobale.Bibliotheque;
import VueGlobale.Livre;
import VueGlobale.Univers;
import Elements.Personnage;
import Elements.Lieux;
import Elements.Evenement;

public class Main {
    public static void main(String[] args) {

        // ========== TESTS SUR LE CALENDRIER ==========
        Date mini = new Date(5);
        mini.changervaleur(0, 2000);
        mini.changervaleur(1, 1);
        mini.changervaleur(2, 1);
        mini.changervaleur(3, 0);
        mini.changervaleur(4, 0);

        Date maxi = new Date(5);
        maxi.changervaleur(0, 2099);
        maxi.changervaleur(1, 12);
        maxi.changervaleur(2, 31);
        maxi.changervaleur(3, 23);
        maxi.changervaleur(4, 59);

        Calendrier cal = new Calendrier(maxi, mini);
        System.out.println("=== TESTS DE CHANGERDATE ===\n");

        Date d1 = new Date(5);
        d1.changervaleur(0, 2020);
        d1.changervaleur(1, 1);
        d1.changervaleur(2, 1);
        d1.changervaleur(3, 0);
        d1.changervaleur(4, 0);

        Date d2 = new Date(5);
        d2.changervaleur(0, 2025);
        d2.changervaleur(1, 6);
        d2.changervaleur(2, 15);
        d2.changervaleur(3, 12);
        d2.changervaleur(4, 30);

        Date d3 = new Date(5);
        d3.changervaleur(0, 2030);
        d3.changervaleur(1, 12);
        d3.changervaleur(2, 31);
        d3.changervaleur(3, 23);
        d3.changervaleur(4, 59);

        cal.ajouterDate(d1);
        cal.ajouterDate(d2);
        cal.ajouterDate(d3);

        // Test initial
        boolean initialOk = compare(cal, new int[]{2020, 2025, 2030});
        System.out.print("Grille initiale : ");
        if (initialOk) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
            System.out.println("Erreur : la grille initiale n'est pas correcte.");
            return;
        }

        // Test 1
        System.out.print("Test 1 : Modifier l'index 1 (2025) en 2023 ... ");
        Date n1 = new Date(5);
        n1.changervaleur(0, 2023);
        n1.changervaleur(1, 3);
        n1.changervaleur(2, 10);
        n1.changervaleur(3, 8);
        n1.changervaleur(4, 0);
        cal.changerdate(1, n1);
        if (compare(cal, new int[]{2020, 2023, 2030})) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        // Test 2
        System.out.print("Test 2 : Modifier l'index 0 (2020) en 2015 ... ");
        Date n2 = new Date(5);
        n2.changervaleur(0, 2015);
        n2.changervaleur(1, 1);
        n2.changervaleur(2, 1);
        n2.changervaleur(3, 0);
        n2.changervaleur(4, 0);
        cal.changerdate(0, n2);
        if (compare(cal, new int[]{2015, 2023, 2030})) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        // Test 3
        System.out.print("Test 3 : Modifier l'index 2 (2030) en 2040 ... ");
        Date n3 = new Date(5);
        n3.changervaleur(0, 2040);
        n3.changervaleur(1, 1);
        n3.changervaleur(2, 1);
        n3.changervaleur(3, 0);
        n3.changervaleur(4, 0);
        cal.changerdate(2, n3);
        if (compare(cal, new int[]{2015, 2023, 2040})) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        // Test 4 : doublon
        System.out.print("Test 4 : Modifier l'index 1 (2023) en 2015 (déjà présent) ... ");
        Date deja = new Date(5);
        deja.changervaleur(0, 2015);
        deja.changervaleur(1, 1);
        deja.changervaleur(2, 1);
        deja.changervaleur(3, 0);
        deja.changervaleur(4, 0);
        cal.changerdate(1, deja);
        if (compare(cal, new int[]{2015, 2023, 2040})) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        // Test 5 : hors bornes
        System.out.print("Test 5 : Modifier l'index 2 (2040) en 2100 (hors bornes) ... ");
        Date hors = new Date(5);
        hors.changervaleur(0, 2100);
        hors.changervaleur(1, 1);
        hors.changervaleur(2, 1);
        hors.changervaleur(3, 0);
        hors.changervaleur(4, 0);
        cal.changerdate(2, hors);
        if (compare(cal, new int[]{2015, 2023, 2040})) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        System.out.print("\nGrille finale (années) : ");
        for (Date d : cal.grille) {
            System.out.print(d.tabdate.get(0).c + " ");
        }
        System.out.println("\n");

        // ========== TESTS SUR LA BIBLIOTHÈQUE ET L'UNIVERS ==========
        System.out.println("=== TESTS SUR LA BIBLIOTHÈQUE ET L'UNIVERS ===\n");

        // 1. Création d'une bibliothèque
        Bibliotheque biblio = new Bibliotheque(1, "Médiathèque Centrale", "Public");

        // 2. Création d'univers
        Univers u1 = new Univers(10, "Terre du Milieu");
        u1.ajouteType("Fantastique");
        Univers u2 = new Univers(11, "Galaxie Lointaine");
        u2.ajouteType("Science-Fiction");

        // 3. Création de personnages
        Personnage p1 = new Personnage(100, "Frodon", "Hobbit de la Comté");
        p1.prenom = "Frodon";
        p1.dateNaissance = 1368;
        p1.dateDeces = 0;

        Personnage p2 = new Personnage(101, "Gandalf", "Magicien Istari");
        p2.prenom = "Gandalf";
        p2.dateNaissance = 0;

        Personnage p3 = new Personnage(102, "Luke", "Jedi et fils d'Anakin");
        p3.prenom = "Skywalker";
        p3.dateNaissance = 19;

        u1.ajoutepersonnage(p1);
        u1.ajoutepersonnage(p2);
        u2.ajoutepersonnage(p3);

        // 4. Création de lieux
        Lieux l1 = new Lieux(200, "Comté", "Village");
        l1.dateConstruction = 1200;
        l1.fondateurId = 100;
        l1.lieuParentId = -1;

        Lieux l2 = new Lieux(201, "Fondcombe", "Vallee");
        l2.dateConstruction = 1000;

        Lieux l3 = new Lieux(202, "Tatooine", "Planète");
        l3.dateConstruction = 0;
        l3.fondateurId = -1;

        u1.ajouteLieux(l1);
        u1.ajouteLieux(l2);
        u2.ajouteLieux(l3);

        // 5. Création d'événements
        Evenement e1 = new Evenement(300, "Conseil d'Elrond", "Réunion des peuples libres");
        e1.types.add("Conseil");
        e1.participantsIds.add(101);

        Evenement e2 = new Evenement(301, "Destruction de l'Anneau", "Bataille finale au Mordor");
        e2.types.add("Guerre");
        e2.participantsIds.add(100);

        u1.ajouteEvenement(e1);
        u1.ajouteEvenement(e2);

        // 6. Création de livres
        Livre livre1 = new Livre(1000, "Le Seigneur des Anneaux", "Fantastique");
        livre1.universliste.add(u1);

        Livre livre2 = new Livre(1001, "Star Wars - Un nouvel espoir", "Science-Fiction");
        livre2.universliste.add(u2);

        biblio.ajoutelivre(livre1);
        biblio.ajoutelivre(livre2);

        // 7. Vérification du nombre de livres dans la bibliothèque
        System.out.print("Vérification : nombre de livres = 2 ... ");
        if (biblio.livreliste.size() == 2) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        // 8. Vérification des contenus des univers
        System.out.print("Vérification : u1 a 2 personnages ... ");
        if (u1.listePersonnages.size() == 2) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        System.out.print("Vérification : u1 a 2 lieux ... ");
        if (u1.listeLieux.size() == 2) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        System.out.print("Vérification : u1 a 2 événements ... ");
        if (u1.listeEvenements.size() == 2) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        System.out.print("Vérification : u2 a 1 personnage ... ");
        if (u2.listePersonnages.size() == 1) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        System.out.print("Vérification : u2 a 1 lieu ... ");
        if (u2.listeLieux.size() == 1) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        System.out.print("Vérification : u2 a 0 événement ... ");
        if (u2.listeEvenements.size() == 0) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        // 9. Test du filtrage par type (Fantastique)
        System.out.print("Filtrage Fantastique : 1 livre attendu ... ");
        ArrayList<Livre> livresFantastiques = biblio.filtragepartype("Fantastique");
        if (livresFantastiques.size() == 1) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        // 10. Test du filtrage par type (Science-Fiction)
        System.out.print("Filtrage Science-Fiction : 1 livre attendu ... ");
        ArrayList<Livre> livresSF = biblio.filtragepartype("Science-Fiction");
        if (livresSF.size() == 1) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        // 11. Test avec un type inexistant
        System.out.print("Filtrage Historique : 0 livre attendu ... ");
        ArrayList<Livre> livresHist = biblio.filtragepartype("Historique");
        if (livresHist.size() == 0) {
            System.out.println("OK");
        } else {
            System.out.println("WARNING");
        }

        System.out.println("\n=== FIN DES TESTS ===");
    }

    // Méthode utilitaire pour comparer les années de la grille
    private static boolean compare(Calendrier cal, int[] attendu) {
        if (cal.grille.size() != attendu.length) return false;
        for (int i = 0; i < attendu.length; i++) {
            if (cal.grille.get(i).tabdate.get(0).c != attendu[i]) return false;
        }
        return true;
    }
}