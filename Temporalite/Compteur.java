package Temporalite;

public class Compteur{
    public int c;
    public Compteur(int n)
    {
        c=n;
    }
    public void ajouter(int a)
    {
        c=c+a;
    }
    public void enlever(int a)
    {
        c=c-a;
    }
}