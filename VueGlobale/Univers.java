package VueGlobale;

import java.util.ArrayList;
import Elements.Personnage;
import Elements.Lieux;
import Elements.Evenement;

public class Univers {
    public int id;
    public String type; // Fantastique, fiction, non fiction...
    public ArrayList<Personnage> listePersonnages;
    public ArrayList<Lieux> listeLieux;
    public ArrayList<Evenement> listeEvenements;

    public Univers(int id) {
        this.id = id;
        this.listePersonnages = new ArrayList<>();
        this.listeLieux = new ArrayList<>();
        this.listeEvenements = new ArrayList<>();
    }

    public void ajouteType(String type) {
        this.type=type;
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