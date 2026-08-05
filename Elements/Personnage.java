package Elements;

import Temporalite.Date;
import java.util.ArrayList;

public class Personnage {
    public int id;
    public String nom;
    public String prenom;
    public Date dateNaissance;
    public Date dateDeces;
    public String description;
    public ArrayList<Lien>liensociaux;
    public Lien lien;
    //public Qualite qualite; // peut etre utile pour decelemr incoherence 

    // Constructeur
    public Personnage(int id, String nom,String prenom, String description) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.description = description;
    }
    public boolean estcequevivant(Date comp) //0 si non 1 si oui
    {
        if (dateDeces.compareTo(comp)>=0)
        {
            return false;
        }
        return true;
    }
    public void naissance(Date n)
    {
        dateNaissance=n;
    }
    public void mort (Date m)
    {
        dateDeces=m;
    }
    public void ajoutlien(Personnage a, Lien l)
    {
            // verifier que le lien n'existe pas en double, verifier que le lien est possible 
    }
    public void changerdesc(String desc)
    {
        description=desc;
    }

}
