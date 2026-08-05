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

    private static int okCount = 0;
    private static int koCount = 0;

    private static void verifier(String libelle, boolean condition) {
        System.out.print(libelle + " ... ");
        if (condition) {
            System.out.println("OK");
            okCount++;
        } else {
            System.out.println("WARNING");
            koCount++;
        }
    }

    private static boolean compareGrille(Calendrier cal, int[] anneesAttendues) {
        if (cal.grille.size() != anneesAttendues.length) return false;
        for (int i = 0; i < anneesAttendues.length; i++) {
            if (cal.grille.get(i).tabdate.get(0).c != anneesAttendues[i]) return false;
        }
        return true;
    }

    public static void main(String[] args) {

        // ========== TESTS CALENDRIER ==========
        System.out.println("=== TESTS CALENDRIER ===\n");

        Date mini = new Date(2000, 1, 1);
        Date maxi = new Date(2099, 12, 31);
        Calendrier cal = new Calendrier(maxi, mini);

        Date d1 = new Date(2020, 1, 1);
        Date d2 = new Date(2025, 6, 15);
        Date d3 = new Date(2030, 12, 31);

        cal.ajouterDate(d1);
        cal.ajouterDate(d2);
        cal.ajouterDate(d3);

        verifier("Grille initiale triee (2020,2025,2030)", compareGrille(cal, new int[]{2020, 2025, 2030}));

        Date n1 = new Date(2023, 3, 10);
        cal.changerdate(1, n1);
        verifier("changerdate index 1 -> 2023", compareGrille(cal, new int[]{2020, 2023, 2030}));

        Date deja = new Date(2020, 1, 1);
        cal.changerdate(1, deja); // doublon volontaire
        verifier("changerdate refuse le doublon", compareGrille(cal, new int[]{2020, 2023, 2030}));

        Date hors = new Date(2200, 1, 1);
        cal.changerdate(0, hors); // hors bornes volontaire
        verifier("changerdate refuse hors bornes", compareGrille(cal, new int[]{2020, 2023, 2030}));

        // ========== TESTS PERSONNAGE ==========
        System.out.println("\n=== TESTS PERSONNAGE ===\n");

        Personnage p1 = new Personnage(100, "Baggins", "Frodon", "Porteur de l'Anneau");
        p1.naissance(new Date(1368, 1, 1));

        verifier("p1 vivant en 1400 (pas de deces enregistre)", p1.estcequevivant(new Date(1400, 1, 1)));

        p1.mort(new Date(1421, 1, 1));
        verifier("p1 vivant en 1400 (avant sa mort)", p1.estcequevivant(new Date(1400, 1, 1)));
        verifier("p1 mort en 1500 (apres sa mort)", !p1.estcequevivant(new Date(1500, 1, 1)));

        Personnage p2 = new Personnage(101, "Le Gris", "Gandalf", "Magicien Istari");
        Personnage p3 = new Personnage(102, "Skywalker", "Luke", "Jedi");

        // ========== TESTS LIEN ==========
        System.out.println("\n=== TESTS LIEN ===\n");

        Lieux comte = new Lieux(200, "Comte", "Village");

        comte.lien.creelien(p1);
        comte.lien.creelien(p2);
        verifier("comte.lien contient 2 personnages", comte.lien.recuperepers().size() == 2);
        verifier("comte.lien.estdedans(p1)", comte.lien.estdedans(p1));
        verifier("comte.lien.estdedans(p3) est faux (jamais ajoute)", !comte.lien.estdedans(p3));

        comte.lien.creelien(p1); // doublon volontaire
        verifier("comte.lien refuse le doublon (toujours 2)", comte.lien.recuperepers().size() == 2);

        // ========== TESTS UNIVERS / LIEUX / EVENEMENT ==========
        System.out.println("\n=== TESTS UNIVERS ===\n");

        Univers u1 = new Univers(10, "Terre du Milieu");
        u1.ajouteType("Fantastique");
        Univers u2 = new Univers(11, "Galaxie Lointaine");
        u2.ajouteType("Science-Fiction");

        u1.ajoutepersonnage(p1);
        u1.ajoutepersonnage(p2);
        u2.ajoutepersonnage(p3);

        Lieux fondcombe = new Lieux(201, "Fondcombe", "Vallee");
        Lieux tatooine = new Lieux(202, "Tatooine", "Planete");

        u1.ajouteLieux(comte);
        u1.ajouteLieux(fondcombe);
        u2.ajouteLieux(tatooine);

        Evenement e1 = new Evenement(300, "Conseil d'Elrond", "Reunion des peuples libres");
        e1.ajouttypeevenement("Conseil");
        e1.lien.creelien(p2); // Gandalf participe

        Evenement e2 = new Evenement(301, "Destruction de l'Anneau", "Bataille finale au Mordor");
        e2.ajouttypeevenement("Guerre");
        e2.lien.creelien(p1); // Frodon participe

        u1.ajouteEvenement(e1);
        u1.ajouteEvenement(e2);

        verifier("u1 a 2 personnages", u1.listePersonnages.size() == 2);
        verifier("u1 a 2 lieux", u1.listeLieux.size() == 2);
        verifier("u1 a 2 evenements", u1.listeEvenements.size() == 2);
        verifier("u2 a 1 personnage", u2.listePersonnages.size() == 1);
        verifier("u2 a 1 lieu", u2.listeLieux.size() == 1);
        verifier("u2 a 0 evenement", u2.listeEvenements.size() == 0);
        verifier("e1.lien contient p2 (Gandalf)", e1.lien.estdedans(p2));
        verifier("e2.lien contient p1 (Frodon)", e2.lien.estdedans(p1));

        // ========== TESTS BIBLIOTHEQUE / LIVRE ==========
        System.out.println("\n=== TESTS BIBLIOTHEQUE ===\n");

        Bibliotheque biblio = new Bibliotheque(1, "Mediatheque Centrale", "Public");

        Livre livre1 = new Livre(1000, "Le Seigneur des Anneaux", "Fantastique");
        livre1.universliste.add(u1);

        Livre livre2 = new Livre(1001, "Star Wars", "Science-Fiction");
        livre2.universliste.add(u2);

        biblio.ajoutelivre(livre1);
        biblio.ajoutelivre(livre2);

        verifier("bibliotheque contient 2 livres", biblio.livreliste.size() == 2);

        ArrayList<Livre> fantastiques = biblio.filtragepartype("Fantastique");
        verifier("filtrage Fantastique -> 1 livre", fantastiques.size() == 1);

        ArrayList<Livre> sf = biblio.filtragepartype("Science-Fiction");
        verifier("filtrage Science-Fiction -> 1 livre", sf.size() == 1);

        ArrayList<Livre> historique = biblio.filtragepartype("Historique");
        verifier("filtrage Historique -> 0 livre", historique.size() == 0);

        // ========== RESUME ==========
        System.out.println("\n=== RESUME ===");
        System.out.println(okCount + " test(s) OK, " + koCount + " test(s) en echec.");
    }
}