package Elements;
import java.util.ArrayList;

public class Lieux {
    public int id;
    public String nom;
    public int dateConstruction;
    public int dateDestruction;
    public int fondateurId;
    public String type;               // ex: "Ville", "Région", "Quartier"
    public int lieuParentId;
    public Lien lien;
    
    public Lieux(int id, String nom, String type) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.lien = new Lien();
    }
}