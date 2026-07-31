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
        this.livreliste = new ArrayList<>();
    }

    public void ajoutelivre(Livre l) {
        livreliste.add(l);
    }
    public ArrayList<Livre> filtragepartype(String type)
    {
        ArrayList<VueGlobale.Livre> livreret = new ArrayList<>();
        for (int i=0; i<livreliste.size(); i++) {
            if (type.equals(livreliste.get(i).type)) {
                livreret.add(livreliste.get(i));
            }
        }
        return livreret;
    }
}