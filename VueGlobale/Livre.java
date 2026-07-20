package VueGlobale;

import java.util.ArrayList;

public class Livre {
    public int id;
    public String nom;
    public String type;
    public ArrayList<Univers> universliste;

    public Livre(int id, String nom, String type) {
        this.id = id;
        this.nom = nom;
        this.type = type;
    }
}