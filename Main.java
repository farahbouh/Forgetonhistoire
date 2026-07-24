import Temporalite.Calendrier;
import Temporalite.Date;

public class Main {
    public static void main(String[] args) {

        // Création du calendrier (taille 5, mini=2000, maxi=2099)
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
        System.out.println("=== DÉBUT DES TESTS ===\n");

        // -------------------------------------------------------------
        // TEST 1 : Insertion de 3 dates valides (vérifier le tri)
        // -------------------------------------------------------------
        Date d1 = new Date(5);
        d1.changervaleur(0, 2025);
        d1.changervaleur(1, 6);
        d1.changervaleur(2, 15);
        d1.changervaleur(3, 14);
        d1.changervaleur(4, 30);

        Date d2 = new Date(5);
        d2.changervaleur(0, 2023);
        d2.changervaleur(1, 12);
        d2.changervaleur(2, 25);
        d2.changervaleur(3, 8);
        d2.changervaleur(4, 0);

        Date d3 = new Date(5);
        d3.changervaleur(0, 2027);
        d3.changervaleur(1, 1);
        d3.changervaleur(2, 1);
        d3.changervaleur(3, 0);
        d3.changervaleur(4, 5);

        cal.ajouterDate(d1);
        cal.ajouterDate(d2);
        cal.ajouterDate(d3);

        // Vérification du tri
        boolean triOk = cal.grille.size() == 3
                && cal.grille.get(0).tabdate.get(0).c == 2023
                && cal.grille.get(1).tabdate.get(0).c == 2025
                && cal.grille.get(2).tabdate.get(0).c == 2027;

        System.out.println("Test 1 - Insertion et tri : " + (triOk ? " OK" : " ÉCHEC"));

        // -------------------------------------------------------------
        // TEST 2 : Doublon (ne doit pas être ajouté)
        // -------------------------------------------------------------
        int tailleAvant = cal.grille.size();
        Date d1_bis = new Date(5);
        d1_bis.changervaleur(0, 2025);
        d1_bis.changervaleur(1, 6);
        d1_bis.changervaleur(2, 15);
        d1_bis.changervaleur(3, 14);
        d1_bis.changervaleur(4, 30);

        cal.ajouterDate(d1_bis);
        boolean doublonOk = cal.grille.size() == tailleAvant; // taille inchangée = OK

        System.out.println("Test 2 - Doublon refusé : " + (doublonOk ? " OK" : " ÉCHEC (date ajoutée alors qu'elle existe)"));

        // -------------------------------------------------------------
        // TEST 3 : Date de taille 3 (doit être refusée)
        // -------------------------------------------------------------
        tailleAvant = cal.grille.size();
        Date dTailleErreur = new Date(3);
        dTailleErreur.changervaleur(0, 2024);
        dTailleErreur.changervaleur(1, 5);
        dTailleErreur.changervaleur(2, 10);

        try {
            cal.ajouterDate(dTailleErreur);
            // Si on arrive ici, c'est qu'elle a été acceptée → erreur
            boolean tailleOk = cal.grille.size() == tailleAvant;
            System.out.println("Test 3 - Taille différente refusée : " + (tailleOk ? " OK" : " ÉCHEC (date ajoutée alors qu'elle a une taille différente)"));
        } catch (IllegalArgumentException e) {
            // Exception levée = bon comportement
            System.out.println("Test 3 - Taille différente refusée :  OK (exception levée)");
        }

        // -------------------------------------------------------------
        // TEST 4 : Date hors intervalle (2100 > maxi)
        // -------------------------------------------------------------
        tailleAvant = cal.grille.size();
        Date dHors = new Date(5);
        dHors.changervaleur(0, 2100);
        dHors.changervaleur(1, 1);
        dHors.changervaleur(2, 1);
        dHors.changervaleur(3, 0);
        dHors.changervaleur(4, 0);

        try {
            cal.ajouterDate(dHors);
            boolean horsOk = cal.grille.size() == tailleAvant;
            System.out.println("Test 4 - Hors intervalle (2100) refusé : " + (horsOk ? " OK" : " ÉCHEC (date ajoutée alors qu'elle est hors intervalle)"));
        } catch (IllegalArgumentException e) {
            System.out.println("Test 4 - Hors intervalle (2100) refusé :  OK (exception levée)");
        }

        // -------------------------------------------------------------
        // TEST 5 : Date sous le mini (1999 < mini)
        // -------------------------------------------------------------
        tailleAvant = cal.grille.size();
        Date dBas = new Date(5);
        dBas.changervaleur(0, 1999);
        dBas.changervaleur(1, 12);
        dBas.changervaleur(2, 31);
        dBas.changervaleur(3, 23);
        dBas.changervaleur(4, 59);

        try {
            cal.ajouterDate(dBas);
            boolean basOk = cal.grille.size() == tailleAvant;
            System.out.println("Test 5 - Sous le mini (1999) refusé : " + (basOk ? " OK" : " ÉCHEC (date ajoutée alors qu'elle est sous le mini)"));
        } catch (IllegalArgumentException e) {
            System.out.println("Test 5 - Sous le mini (1999) refusé :  OK (exception levée)");
        }

        // -------------------------------------------------------------
        // Affichage final de la grille
        // -------------------------------------------------------------
        System.out.print("\nContenu final de la grille (années) : ");
        for (Date d : cal.grille) {
            System.out.print(d.tabdate.get(0).c + " ");
        }
        System.out.println();
    }
}