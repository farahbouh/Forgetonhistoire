package Temporalite;
import java.util.ArrayList;

public class Date
{
    String type;
    int taille; // Combien d'element definit une date jour heure les 2 ect..
    public ArrayList<Compteur> tabdate; // peut contenir jour, mois annee et meme plus meme heure
    Date(int t)
    {
        taille=t;
        tabdate = new ArrayList<>();
        for (int i = 0; i < taille; i++) {
            tabdate.add(new Compteur(0));
        }
    }
    public void ajoutunevaleur(int position,int valeur)
    {
        if (valeur<taille)
        {
            tabdate[position].i=valeur;
        }
    }
}