import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {

	//ATTRIBUT
    public class State implements Comparable<State> {
        Localisation node;
        double time;
        double speed;

        public State(Localisation node, double time, double speed) {
            this.node = node;
            this.time = time;
            this.speed = speed;
        }

        @Override
        public int compareTo(State other) {
            return Double.compare(this.time, other.time);
        }
    }

	//TODO
    Map<Long, Localisation> nodes;

    public Graph(String localisations, String roads)  {
        //TODO
        nodes = new HashMap<>();

        nodeInit(localisations);

        rueInit(roads);
    }

    private void nodeInit(String localisations){
        try (BufferedReader br = new BufferedReader(new FileReader(localisations))){
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] raw = line.split(",");
                long id = Long.parseLong(raw[0]);
                String nom = raw[1];
                double latitude = Double.parseDouble(raw[2]);
                double longitude = Double.parseDouble(raw[3]);
                double altitude = Double.parseDouble(raw[4]);
                Localisation localisation = new Localisation(id, latitude, longitude, altitude, nom);
                nodes.put(localisation.getId(), localisation);
            }
        }catch(FileNotFoundException e){
            System.out.println(e);
        }catch(IOException e){
            System.out.println(e);
        }
    }

    private void rueInit(String rues){
        try (BufferedReader br = new BufferedReader(new FileReader(rues))){
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] raw = line.split(",");
                long idSource = Long.parseLong(raw[0]);
                long idTarget = Long.parseLong(raw[1]);
                double distance = Double.parseDouble(raw[2]);
                String nom = raw[3];
                Rue rue = new Rue(nodes.get(idSource), nodes.get(idTarget), distance, nom);
                nodes.get(rue.getDepart().getId()).getRues().add(rue);
            }
        }catch(FileNotFoundException e){
            System.out.println(e);
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public Localisation[] determinerZoneInondee(long[] idsOrigin,double epsilon) {
        Set<Localisation> visited = new HashSet<>();
        Queue<Localisation> queue = new LinkedList<>();
        List<Localisation> result = new ArrayList<>();

        for (long l : idsOrigin) {
            Localisation start = nodes.get(l);
            if(start != null){
            queue.add(start);
            visited.add(start);}
        }

        while (!queue.isEmpty()){
            Localisation current = queue.poll();
            result.add(current);
            for (Rue rue : current.getRues()) {
                Localisation neighbor = rue.getArrive();
                if(!visited.contains(neighbor)){
                    if(neighbor.getAltitude() <= current.getAltitude() + epsilon){
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
                
            }
        }
		return result.toArray(new Localisation[0]);
    }

    public Deque<Localisation> trouverCheminLePlusCourtPourContournerLaZoneInondee(long idOrigin, long idDestination, Localisation[] floodedZone) {
		//TODO
        Set<Localisation> flooded = new HashSet<>(Arrays.asList(floodedZone));
        Map<Localisation, Localisation> parent = new HashMap<>();

        Queue<Localisation> queue = new LinkedList<>();
        Set<Localisation> visited = new HashSet<>();

        Localisation start = nodes.get(idOrigin);
        Localisation end = nodes.get(idDestination);

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()){
            Localisation current = queue.poll();

            if (current.equals(end)){
                Deque<Localisation> path = new LinkedList<>();
                Localisation curr = end;

                while (curr != null){
                    path.addFirst(curr);
                    curr = parent.get(curr);
                }
                return path;
            }

            for (Rue rue : current.getRues()) {
                Localisation neighbor = rue.getArrive();
                if (!visited.contains(neighbor) && !flooded.contains(neighbor)){
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }

            }
        }

        throw new RuntimeException("Pas de chemin trouvé");

    }
public Map<Localisation, Double> determinerChronologieDeLaCrue(long[] idsOrigin, double vWaterInit, double k) {
    // Résultat final : temps d'inondation pour chaque noeud atteint
    Map<Localisation, Double> tFlood = new HashMap<>();

    // PriorityQueue pour traiter le noeud le plus rapide en premier
    PriorityQueue<State> pq = new PriorityQueue<>();

    // Initialisation : sources de l’inondation
    for (long id : idsOrigin) {
        Localisation start = nodes.get(id);
        if (start != null) {
            double time = 0.0; // source déjà inondée à t=0
            tFlood.put(start, time);
            pq.add(new State(start, time, vWaterInit));
        }
    }

    // Boucle principale de propagation
    while (!pq.isEmpty()) {
        State current = pq.poll();
        Localisation node = current.node;

        // Si un meilleur temps est déjà enregistré, on ignore
        if (current.time > tFlood.get(node)) continue;

        // Parcours des voisins
        for (Rue rue : node.getRues()) {
            Localisation neighbor = rue.getArrive();

            // Calcul de la vitesse finale sur le voisin
            double newSpeed;
            double altitudeDiff = neighbor.getAltitude() - node.getAltitude();
            double S = rue.getPente();
            if (altitudeDiff < 0) {
                // pente descendante
                newSpeed = current.speed + k * S;
            } else {
                // pente montante
                newSpeed = current.speed - k * S;
            }

            // Si la vitesse finale est négative ou nulle, l’eau ne peut pas se propager
            if (newSpeed <= 0) continue;

            // Calcul du temps pour atteindre le voisin
            double newTime = current.time + rue.getDistance() / newSpeed;

            // Mise à jour si c’est le premier passage ou un temps plus rapide
            if (!tFlood.containsKey(neighbor) || newTime < tFlood.get(neighbor)) {
                tFlood.put(neighbor, newTime);
                pq.add(new State(neighbor, newTime, newSpeed));
            }
        }
    }

    // --- TRI CHRONOLOGIQUE ---
    List<Map.Entry<Localisation, Double>> chronologie = new ArrayList<>(tFlood.entrySet());
    chronologie.sort(Map.Entry.comparingByValue()); // tri par temps d'inondation croissant

    // Création d'une LinkedHashMap pour garder l'ordre
    Map<Localisation, Double> tFloodSorted = new LinkedHashMap<>();
    for (Map.Entry<Localisation, Double> entry : chronologie) {
        tFloodSorted.put(entry.getKey(), entry.getValue());
    }

    return tFloodSorted; // Map<Localisation, Double> triée par temps croissant
}

    public Deque<Localisation> trouverCheminDEvacuationLePlusCourt(long idOrigin, long idEvacuation, double vVehicule, Map<Localisation,Double> tFlood) {
        Map<Long, Double> tempsPourLoc = new HashMap<>();
        Map<Long, Double> tempsPourLocDef = new HashMap<>();
        Map<Localisation, Localisation> parent = new HashMap<>();

        Long current = idOrigin;
        //tempsPourLoc.put(current, null);
        tempsPourLocDef.put(current, 0.0);
        parent.put(nodes.get(current), null);

        Long smallestId = -1L;
        while(smallestId != idOrigin){
            Double tcurrent = tempsPourLocDef.get(current);
            for(Rue rue: nodes.get(current).getRues()){
                Long arriverId = rue.getArrive().getId();
                if(tempsPourLocDef.containsKey(arriverId)){
                    continue;
                }
                Double temp = rue.getDistance()/vVehicule + tcurrent;
                Localisation arrive = nodes.get(arriverId);
                if((!tempsPourLoc.containsKey(arriverId) || tempsPourLoc.get(arriverId) > temp) && (!tFlood.containsKey(arrive) || temp < tFlood.get(arrive))){
                    tempsPourLoc.put(arriverId, temp);
                    parent.put(arrive, nodes.get(current));
                }
            }

            Double smallest = Double.MAX_VALUE;
            smallestId = idOrigin;
            for(Long next: tempsPourLoc.keySet()){
                Double temp = tempsPourLoc.get(next);
                if(temp < smallest){
                    smallest = temp;
                    smallestId = next;
                }
            }

            if(smallestId == idEvacuation) {
                Deque<Localisation> path = new LinkedList<Localisation>();
                path.addFirst(nodes.get(smallestId));
                Localisation follow = parent.get(nodes.get(smallestId));
                while(follow != null){
                    path.addFirst(follow);
                    follow = parent.get(follow);
                }

                return path;
            }

            current = smallestId;
            tempsPourLocDef.put(current, tempsPourLoc.get(current));
            tempsPourLoc.remove(current);
        }

		throw new RuntimeException("pas de chemin entre " + idOrigin + " et " + idEvacuation);

    }

}
