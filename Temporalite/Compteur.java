package Temporalite;

public class Compteur{
    public int i;
    Compteur(int n)
    {
        i=n;
    }
    public void ajouter(int a)
    {
        i=i+a;
    }
    public void enlever(int a)
    {
        i=i-a;
    }

}