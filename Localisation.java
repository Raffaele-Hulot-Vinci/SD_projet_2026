import java.util.HashSet;
import java.util.Map;

public class Localisation {
    private final long id;
    private final double latitude, longitude, altitude;
    private final String nom;

    private HashSet<Rue> rues;

    public Localisation(long id, double latitude, double longitude, double altitude, String nom) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.nom = nom;

        rues = new HashSet<>();
    }

    public long getId() {
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

    public HashSet<Rue> getRues(){
        return rues;
    }
}
