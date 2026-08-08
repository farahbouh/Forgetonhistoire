package Elements;
import java.util.ArrayList;
import Temporalite.Date;

public class Lieux {
    public int id;
    public ArrayList<String> nom; // Liste car certains lieux peuvent se transformer ex Lugdunum devient Lyon
    public ArrayList<Date> dateConstruction;
    public ArrayList<Date> dateDestruction;
    public int fondateurId;
    public ArrayList<String> type;               // ex: "Ville", "Région", "Quartier"
    public int lieuParentId;
    public Lien lien;
    
    public Lieux(int id, String nom, String type) {
        this.id = id;
        this.nom = new ArrayList<>();
        this.type = new ArrayList<>() ;
        this.lien = new Lien();

        this.nom.add(nom);
        this.type.add(type);
    }
    public Lieux(int id)
    {
        this.id = id;
        this.nom = new ArrayList<>();
        this.type = new ArrayList<>() ;
        this.lien = new Lien();
    }
    public boolean ajoutelieux(String nom, String type, Date dest, Date cons)
    {
        // La date de destruction de l'ancien lieu peut differer de la date de construction
        // du nouveau : un "trou" temporel entre les deux est possible (le lieu peut etre
        // abandonne un temps avant qu'un nouveau lieu ne soit construit au meme endroit).
        // La nouvelle periode est inseree dans l'ordre chronologique, apres verification
        // qu'elle ne chevauche aucune periode deja existante.
        if (dest.compareTo(cons)>=0)
        {
            return false; // date à l'envers
        }
        for (int i=0;i<this.nom.size();i++)
        {
            if (!(dest.compareTo(dateConstruction.get(i)) <= 0 || dateDestruction.get(i).compareTo(cons) <= 0)) {
                return false; // chevauchement avec la periode i
            }

            if (dest.compareTo(dateConstruction.get(i)) <= 0 || dateDestruction.get(i).compareTo(cons) <= 0) {
                this.nom.add(i+1,nom);
                this.type.add(i+1,type);
                this.dateConstruction.add(i+1,cons);
                this.dateDestruction.add(i+1,dest);

                return true;
            }
        }

        return false;
    }
    public void supprimelieux(int i)
    {
        // decaler tous les elements
        this.nom.remove(i);
        this.type.remove(i);
        this.dateConstruction.remove(i);
        this.dateDestruction.remove(i);

    }
}