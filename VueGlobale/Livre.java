package VueGlobale;
import java.util.ArrayList;

public class Livre {
    public int id;
    public String nom;
    public String type;
    public String description;
    public ArrayList<Univers> universliste;

    public Livre(int id, String nom, String type) {
        this(id, nom, type, "");
    }

    public Livre(int id, String nom, String type, String description) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.universliste = new ArrayList<>();
    }
}