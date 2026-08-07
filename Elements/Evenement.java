package Elements;
import Temporalite.Date;
import java.util.ArrayList;

public class Evenement {
    public int id;
    public String titre;
    public Date dateDebut;
    public Date dateFin;
    public ArrayList<String> types;   // ex: ["Guerre", "Mariage", "Conseil"]
    public String description;
    public Lien lien;

    // Constructeur
    public Evenement(int id, String titre, String description) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.types = new ArrayList<>();
        this.lien = new Lien();
    }
    public Evenement(int id) {
        this.id = id;
        this.titre = "titre";
        this.description = "description";
        this.types = new ArrayList<>();
        this.lien = new Lien();
    }

    public void ajouttypeevenement(String type)
    {
        this.types.add(type);
    }
    public void ajoutedate(Date d, Date f) {
        if (d.compareTo(f) <= 0) {
            dateDebut = d;
            dateFin = f;
        }
    }
    public void ajoutdesc(String desc)
    {
        this.description=desc;
    }
    public void supptypeevenement(String type)
    {
        for (int i=0; i< types.size();i++) {
            if (this.types.get(i).equals(type))
                this.types.remove(i);
        }
    }
    // comment ajouter un personnage a un evenement ?? (Decuplement ?)
}