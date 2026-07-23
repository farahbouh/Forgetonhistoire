import Temporalite.Compteur;
import Temporalite.Date;
import Temporalite.Calendrier;

public class Main {
    public static void main(String[] args) {

        // --- Test Compteur ---
        System.out.println("=== Test Compteur ===");
        Compteur c = new Compteur(5);
        System.out.println("Valeur initiale : " + c.c);
        c.ajouter(3);
        System.out.println("Apres ajouter(3) : " + c.c);
        c.enlever(2);
        System.out.println("Apres enlever(2) : " + c.c);

        // --- Test Date : changervaleur ---
        System.out.println("\n=== Test Date.changervaleur ===");
        Date d1 = new Date(1);
        d1.changervaleur(0, 2020);
        System.out.println("d1 = " + d1.tabdate.get(0).c);

        // --- Test Date : changerdate ---
        System.out.println("\n=== Test Date.changerdate ===");
        Date d2 = new Date(1);
        d2.changervaleur(0, 2025);
        d1.changerdate(d2);
        System.out.println("d1 apres changerdate(d2) = " + d1.tabdate.get(0).c);

        // --- Test Date : compareTo ---
        System.out.println("\n=== Test Date.compareTo ===");
        Date a = new Date(1);
        a.changervaleur(0, 2020);
        Date b = new Date(1);
        b.changervaleur(0, 2025);
        System.out.println("a.compareTo(b) = " + a.compareTo(b));
        System.out.println("b.compareTo(a) = " + b.compareTo(a));
        System.out.println("a.compareTo(a) = " + a.compareTo(a));

        // --- Test Calendrier : datevalable ---
        System.out.println("\n=== Test Calendrier.datevalable ===");
        Date mini = new Date(1);
        mini.changervaleur(0, 2000);
        Date maxi = new Date(1);
        maxi.changervaleur(0, 9999);
        Calendrier cal = new Calendrier(maxi, mini);
        System.out.println("datevalable(mini, maxi) = " + cal.datevalable(mini, maxi));
        System.out.println("datevalable(maxi, mini) = " + cal.datevalable(maxi, mini));

        // --- Test Calendrier : ajouterDate ---
        System.out.println("\n=== Test Calendrier.ajouterDate ===");
        Date e1 = new Date(1);
        e1.changervaleur(0, 2023);
        Date e2 = new Date(1);
        e2.changervaleur(0, 2021);
        Date e3 = new Date(1);
        e3.changervaleur(0, 2025);

        cal.ajouterDate(e1);
        cal.ajouterDate(e2);
        cal.ajouterDate(e3);

        System.out.print("Contenu de grille : ");
        for (Date d : cal.grille) {
            System.out.print(d.tabdate.get(0).c + " ");
        }
        System.out.println();

        // --- Test Calendrier : ajouterDate sur doublon ---
        System.out.println("\n=== Test Calendrier.ajouterDate (doublon) ===");
        int tailleAvant = cal.grille.size();
        Date e1bis = new Date(1);
        e1bis.changervaleur(0, 2023);
        cal.ajouterDate(e1bis);
        System.out.println("Taille avant = " + tailleAvant + ", taille apres = " + cal.grille.size());
    }
}