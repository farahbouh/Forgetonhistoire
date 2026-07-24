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

    public void changerdate(int i,Date nouve)
    {
        if (grille.isEmpty() || grille.size()<i+1) return;

        /*if (nouve.taille != debut.taille) {
            System.out.println("Taille incorrecte");
            return;
        }
        if (nouve.compareTo(debut) < 0 || nouve.compareTo(fin) > 0) {
            System.out.println("Nouvelle date hors bornes");
            return;
        }
        Date d=grille.get(i);
        if ((d.compareTo(nouve)==0) || (d.compareTo(grille.get(i-1))>0 && d.compareTo(grille.get(i+1))<0))
        {
            System.out.println("Ca va pas");
            return;
        }*/
        // index
        if (i < 0 || i >= grille.size()) {
            System.out.println("Index invalide");
            return;
        }

        // Taille
        if (nouve.taille != this.debut.taille) {
            System.out.println("Taille incorrecte");
            return;
        }

        // Bornes justes
        if (nouve.compareTo(debut) < 0 || nouve.compareTo(fin) > 0) {
            System.out.println("Nouvelle date hors bornes");
            return;
        }

        // Doublon
        for (int j = 0; j < grille.size(); j++) {
            if (j != i && grille.get(j).compareTo(nouve) == 0) {
                System.out.println("Cette date existe déjà, modification annulée");
                return;
            }
        }

        // Date inchangée ?
        if (grille.get(i).compareTo(nouve) == 0) {
            System.out.println("Date inchangée");
            return;
        }

        // on retire l'ancienne
        grille.remove(i);

        // On réinsère la nouve
        ajouterDate(nouve);
    }



    public void ajouterDate(Date d) {
        if ((d.compareTo(debut))<0 || (d.compareTo(fin))>0)
        {
            System.out.println("Date hors borne ajoute max ou reduire le min");
            return;
        }
        int i=0;
        while (i < grille.size() && grille.get(i).compareTo(d) < 0)
        {
            if (d.compareTo(grille.get(i)) == 0)
            {
                System.out.println("Date existante");
                return;
            }
            i++;
        } // compare chaque date jusqu'a que l'ecart soit positif
        if (i < grille.size() && grille.get(i).compareTo(d) == 0) {
            System.out.println("Date existante");
            return;
        }
        grille.add(i, d);
    }
}