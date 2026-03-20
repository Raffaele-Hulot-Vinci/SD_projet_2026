public class Rue {
    private final Localisation depart, arrive;
    private final double distance, pente;
    private final String nom;

    public Rue(Localisation depart, Localisation arrive, int distance, String nom) {
        this.depart = depart;
        this.arrive = arrive;
        this.distance = distance;
        this.nom = nom;

        pente = ((Double.max(depart.getAltitude(), arrive.getAltitude()) - Double.min(depart.getAltitude(), arrive.getAltitude())) / distance);
    }

    public Localisation getDepart() {
        return depart;
    }

    public Localisation getArrive() {
        return arrive;
    }

    public double getDistance() {
        return distance;
    }

    public String getNom() {
        return nom;
    }
}
