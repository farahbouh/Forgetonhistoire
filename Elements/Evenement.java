package Elements;

import java.util.ArrayList;

public class Evenement {
    public int id;
    public String titre;
    public String dateDebut;
    public String dateFin;
    public ArrayList<String> types;   // ex: ["Guerre", "Mariage", "Conseil"]
    public String description;
    public ArrayList<Integer> participantsIds;

    // Constructeur
    public Evenement(int id, String titre, String description) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.types = new ArrayList<>();
        this.participantsIds = new ArrayList<>();
    }
}