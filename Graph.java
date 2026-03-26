import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {

	//ATTRIBUT
    private static class State {
        Localisation node;
        double time;
        double speed;

        public State(Localisation node, double time, double speed) {
            this.node = node;
            this.time = time;
            this.speed = speed;
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

    public void rueInit(String rues){
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

            if (current.equals(end)) break;

            for (Rue rue : current.getRues()) {
                Localisation neighbor = rue.getArrive();
                if (!visited.contains(neighbor) && !flooded.contains(neighbor)){
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }

            }
        }

        Deque<Localisation> path = new LinkedList<>();
        Localisation curr = end;

        while (curr != null){
            path.addFirst(curr);
            curr = parent.get(curr);
        }
        return path;
    }

    public Map<Localisation,Double> determinerChronologieDeLaCrue(long[] idsOrigin, double vWaterInit, double k) {
        //TODO
        // Résultat final
        HashMap<Localisation,Double> tFlood = new HashMap<>();
        // PriorityQueue pour traiter le noeud le plus rapide en premier
        PriorityQueue<State> pq = new PriorityQueue<>();

        // Initialisation : sources de l’inondation
        for (long l : idsOrigin) {
            Localisation start = nodes.get(l);

            if (start != null) {

                double time = 0; // sources déjà inondées → t=0
                double speed = vWaterInit;

                tFlood.put(start, time);
                pq.add(new State(start, time, speed));
            }
        }

        // Boucle principale de propagation (Dijkstra adapté au temps)
        while (!pq.isEmpty()) {
            
            State current = pq.poll();

            // Si un meilleur temps est déjà enregistré pour ce noeud, on ignore
            if (current.time > tFlood.get(current.node)) continue;

            // Parcours des voisins
            for (Rue rue : current.node.getRues()) {
                Localisation neighbor = rue.getArrive();

                double newSpeed = current.speed + k * rue.getPente();

                // Si la vitesse est nulle ou negative, l'eau ne peut pas passer
                if (newSpeed <= 0) continue;

                // Calcul du temps pour atteindre le voisin
                double newTime = current.time + rue.getDistance() / newSpeed;

                // Condition Dijkstra : si voisin jamais atteint ou meilleur temps trouvé
                if (!tFlood.containsKey(neighbor) || newTime < tFlood.get(neighbor)) {
                    tFlood.put(neighbor, newTime);                     // mise à jour du temps minimal
                    pq.add(new State(neighbor, newTime, newSpeed));    // ajout dans PQ
                }
            }
        }
        return tFlood ;
    }

    public Deque<Localisation> trouverCheminDEvacuationLePlusCourt(long idOrigin, long idEvacuation, double vVehicule, Map<Localisation,Double> tFlood) {
        //TODO
		return null ;
    }


}
