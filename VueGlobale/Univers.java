package VueGlobale;

import java.util.ArrayList;
import java.time.LocalDateTime;

import Elements.Personnage;
import Elements.Lieux;
import Elements.Evenement;
import Temporalite.Date;
import Temporalite.Calendrier;

public class Univers {
    public int id;
    public String nom;
    public Date date;
    public Calendrier calendrier;
    public String type; // Fantastique, fiction, non fiction...
    public ArrayList<Personnage> listePersonnages;
    public ArrayList<Lieux> listeLieux;
    public ArrayList<Evenement> listeEvenements;

    public Univers(int id) {
        LocalDateTime d = LocalDateTime.now();
        this.date = new Date(d.getYear(), d.getMonthValue(), d.getYear());
        this.id = id;
        this.nom = "defaut";
        this.calendrier = new Calendrier(new Date(9999, 12, 31), new Date(-9999, 1, 1));
        this.listePersonnages = new ArrayList<>();
        this.listeLieux = new ArrayList<>();
        this.listeEvenements = new ArrayList<>();
    }

    public Univers(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.calendrier = new Calendrier(new Date(9999, 12, 31), new Date(-9999, 1, 1));
        this.listePersonnages = new ArrayList<>();
        this.listeLieux = new ArrayList<>();
        this.listeEvenements = new ArrayList<>();
    }

    public void ajouteType(String type) {
        this.type = type;
    }

    public void ajoutepersonnage(Personnage p) {
        listePersonnages.add(p);
    }

    public void ajouteEvenement(Evenement e) {
        listeEvenements.add(e);
    }

    public void ajouteLieux(Lieux l) {
        listeLieux.add(l);
    }
}