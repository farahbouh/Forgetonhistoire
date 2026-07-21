package Temporalite;

import java.util.ArrayList;

public class Calendrier{
    public ArrayList<Date> grille;
    public Date debut;
    public Date fin;
    Calendrier(Date maxi, Date mini)
    {
        if (!datevalable(mini,maxi))
        {
            throw new IllegalArgumentException("mini doit être <= maxi");
        }
        grille = new ArrayList<>();
        debut=mini;
        fin=maxi;
        System.out.println("Calendrier saisie");
    }

    public boolean datevalable(Date deb, Date fin)
    {

        if (deb.taille != fin.taille)
        {
            return false; // 2 date avec des longueur differente
        }
        int i=0;
        while (i<deb.taille)
        {
            if (deb.tabdate[i]<fin.tabdate[i])
            {
                i=i+1;
            }
        }
        return true; // date valide
    }

}