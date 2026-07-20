package Elements;

import java.util.ArrayList;

public class Personnage {
    public int id;
    public String nom;
    public String prenom;
    public int dateNaissance;
    public int dateDeces;
    public String description;
    public ArrayList<Lien>liensociaux;

    // Constructeur
    public Personnage(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }
}
