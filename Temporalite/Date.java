package Temporalite;
import java.util.ArrayList;

public class Date implements Comparable<Date>
{
    String type;
    int taille;
    public ArrayList<Compteur> tabdate;

    public Date(int a,int m, int j)
    {
        taille=3;
        tabdate = new ArrayList<>();
        tabdate.add(new Compteur(a));
        tabdate.add(new Compteur(m));
        tabdate.add(new Compteur(j));
    }
    public Date(int t)
    {
        taille=t;
        tabdate = new ArrayList<>();
        for (int i = 0; i < taille; i++) {
            tabdate.add(new Compteur(0));
        }
    }
    public void changervaleur(int position,int valeur)
    {
        if (position<taille)
        {
            tabdate.get(position).c=valeur;
        }
    }
    public void changerdate(Date valeur)
    {
        if (valeur.taille==taille)
        {   for (int i=0;i<taille;i++)
            {
                changervaleur(i,valeur.tabdate.get(i).c);
            }
        }
    }

    public int compareTo(Date autre) {
        int min = Math.min(this.taille, autre.taille);
        for (int i = 0; i < min; i++) {
            int diff = this.tabdate.get(i).c - autre.tabdate.get(i).c;
            if (diff != 0)
            {
                return diff;
            }
        }
        return this.taille - autre.taille;
    }
}