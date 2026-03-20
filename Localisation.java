
public class Localisation {
    private final int id;
    private final double latitude, longitude, altitude;
    private final String nom;

    public Localisation(int id, double latitude, double longitude, double altitude, String nom) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public String getNom() {
        return nom;
    }
}
