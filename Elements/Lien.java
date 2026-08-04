package Elements;
import java.util.ArrayList;


import static java.lang.Math.max;

public class Lien {
    Object type; //evenement ou personnage ect..
    public ArrayList<Personnage> perstab; //liste des elements lié
    public ArrayList<Lieux> lieuxtab;
    public ArrayList<Evenement> evetab;

    Lien(Object t)
    {
        this.type=t;
        this.perstab=new ArrayList<>();
        this.lieuxtab=new ArrayList<>();
        this.evetab=new ArrayList<>();
    }
    public ArrayList<Personnage> recuperepers() // recup pers
    {
        return perstab;
    }
    public ArrayList<Lieux> recuperelieux() // recup lieux
    {
        return lieuxtab;
    }
    public ArrayList<Evenement> recupereeve() // recup evene
    {
        return evetab;
    }
    public void creelien(Object t)
    {
        if (t instanceof Personnage && !perstab.contains(t)) {
            Personnage p = (Personnage) t;
            perstab.add(p);
        } else if (t instanceof Evenement && !evetab.contains(t)) {
            Evenement e=(Evenement) t;
            evetab.add(e);
        } else if (t instanceof Lieux && !lieuxtab.contains(t)) {
            Lieux l=(Lieux) t;
            lieuxtab.add(l);
        }
    }
    public boolean estdedans(Object t) {

        int maxi = max(perstab.size(), lieuxtab.size());
        maxi = max(maxi, evetab.size());

        for (int i = 0; i < maxi; i++) {
            //verif que la liste n'est pas vide et que l'objet lie existe
            if (t instanceof Personnage && i < perstab.size() && perstab.get(i).equals(t)) {
                return true;
            } else if (t instanceof Evenement && i < evetab.size() && evetab.get(i).equals(t)) {
                return true;
            } else if (t instanceof Lieux && i < lieuxtab.size() && lieuxtab.get(i).equals(t)) {
                return true;
            }
        }
        return false;
    }


}