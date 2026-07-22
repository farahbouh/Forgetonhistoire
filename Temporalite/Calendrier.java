package Temporalite;

import java.util.ArrayList;
import java.util.Collections;

public class Calendrier {
    public ArrayList<Date> grille;
    public Date debut;
    public Date fin;

    public Calendrier(Date maxi, Date mini) {
        if (!datevalable(mini, maxi)) {
            throw new IllegalArgumentException("mini doit etre <= maxi");
        }
        grille = new ArrayList<>();
        debut = mini;
        fin = maxi;
        System.out.println("Calendrier saisie");
    }

    public boolean datevalable(Date deb, Date fin) {
        if (deb.taille != fin.taille) {
            return false;
        }
        int i = 0;
        while (i < deb.taille) {
            if (deb.tabdate.get(i).c > fin.tabdate.get(i).c) {
                return false;
            }
            i = i + 1;
        }
        return true;
    }

    public void ajouterDate(Date d) {
        grille.add(d);
        trier();
    }

    public void trier() {
        Collections.sort(grille);
    }
}