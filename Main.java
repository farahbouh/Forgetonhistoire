import Temporalite.Calendrier;
import Temporalite.Date;

public class Main {
    public static void main(String[] args) {

        // Création du calendrier (5 composants) avec mini=2000, maxi=2099
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

        // Préparer la grille avec trois dates
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

        // Vérification initiale
        if (!compare(cal, new int[]{2020, 2025, 2030})) {
            System.out.println("Erreur : la grille initiale n'est pas correcte.");
            return;
        }

        // Test 1 : modifier l'index 1 (2025) en 2023 -> [2020, 2023, 2030]
        System.out.print("Test 1 : Modifier l'index 1 (2025) en 2023 ... ");
        Date n1 = new Date(5);
        n1.changervaleur(0, 2023);
        n1.changervaleur(1, 3);
        n1.changervaleur(2, 10);
        n1.changervaleur(3, 8);
        n1.changervaleur(4, 0);
        cal.changerdate(1, n1);
        System.out.println(compare(cal, new int[]{2020, 2023, 2030}) ? "OK" : "ECHEC");

        // Test 2 : modifier l'index 0 (2020) en 2015 -> [2015, 2023, 2030]
        System.out.print("Test 2 : Modifier l'index 0 (2020) en 2015 ... ");
        Date n2 = new Date(5);
        n2.changervaleur(0, 2015);
        n2.changervaleur(1, 1);
        n2.changervaleur(2, 1);
        n2.changervaleur(3, 0);
        n2.changervaleur(4, 0);
        cal.changerdate(0, n2);
        System.out.println(compare(cal, new int[]{2015, 2023, 2030}) ? "OK" : "ECHEC");

        // Test 3 : modifier l'index 2 (2030) en 2040 -> [2015, 2023, 2040]
        System.out.print("Test 3 : Modifier l'index 2 (2030) en 2040 ... ");
        Date n3 = new Date(5);
        n3.changervaleur(0, 2040);
        n3.changervaleur(1, 1);
        n3.changervaleur(2, 1);
        n3.changervaleur(3, 0);
        n3.changervaleur(4, 0);
        cal.changerdate(2, n3);
        System.out.println(compare(cal, new int[]{2015, 2023, 2040}) ? "OK" : "ECHEC");

        // Test 4 : modifier l'index 1 (2023) en 2015 (doublon) -> doit être annulé
        System.out.print("Test 4 : Modifier l'index 1 (2023) en 2015 (déjà présent) ... ");
        Date deja = new Date(5);
        deja.changervaleur(0, 2015);
        deja.changervaleur(1, 1);
        deja.changervaleur(2, 1);
        deja.changervaleur(3, 0);
        deja.changervaleur(4, 0);
        cal.changerdate(1, deja);
        System.out.println(compare(cal, new int[]{2015, 2023, 2040}) ? "OK" : "ECHEC");

        // Test 5 : modifier l'index 2 (2040) en 2100 (hors bornes) -> doit être annulé
        System.out.print("Test 5 : Modifier l'index 2 (2040) en 2100 (hors bornes) ... ");
        Date hors = new Date(5);
        hors.changervaleur(0, 2100);
        hors.changervaleur(1, 1);
        hors.changervaleur(2, 1);
        hors.changervaleur(3, 0);
        hors.changervaleur(4, 0);
        cal.changerdate(2, hors);
        System.out.println(compare(cal, new int[]{2015, 2023, 2040}) ? "OK" : "ECHEC");

        // Affichage final
        System.out.print("\nGrille finale (années) : ");
        for (Date d : cal.grille) {
            System.out.print(d.tabdate.get(0).c + " ");
        }
        System.out.println();
    }

    // Méthode utilitaire pour comparer les années de la grille avec un tableau attendu
    private static boolean compare(Calendrier cal, int[] attendu) {
        if (cal.grille.size() != attendu.length) return false;
        for (int i = 0; i < attendu.length; i++) {
            if (cal.grille.get(i).tabdate.get(0).c != attendu[i]) return false;
        }
        return true;
    }
}