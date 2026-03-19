
public class Localisation {
    private int id, latitude, longitude, altitude;
    private String nom;

    public Localisation(int id, int latitude, int longitude, int altitude, String nom) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public int getLatitude() {
        return latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public int getAltitude() {
        return altitude;
    }

    public String getNom() {
        return nom;
    }
}
