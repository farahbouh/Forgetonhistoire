package VueGlobale;
import java.util.ArrayList;

public class Bibliotheque {
    public int id;
    public String nom;
    public String type;
    public ArrayList<Livre> livreliste;

    public Bibliotheque(int id, String nom, String type) {
        this.id = id;
        this.nom = nom;
        this.type = type;
    }
}