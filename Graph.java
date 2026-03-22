import java.util.*;

public class Graph {

	//ATTRIBUT ?
	//TODO
    Map<Long, Localisation> nodes;
    Map<Localisation, HashSet<Rue>> localisationListMap;

    public Graph(String localisations, String roads)  {
        //TODO
        nodes = new HashMap<>();
        localisationListMap = new HashMap<>();

        Scanner scanL = new Scanner(localisations);
        while (scanL.hasNext()){
            String[] rawL = scanL.next().split(",");
            int id = Integer.parseInt(rawL[0]);
            String nom = rawL[1];
            double latitude = Double.parseDouble(rawL[2]);
            double longitude = Double.parseDouble(rawL[3]);
            double altitude = Double.parseDouble(rawL[4]);
            Localisation localisation = new Localisation(id, latitude, longitude, altitude, nom);
            localisationListMap.put(localisation, new HashSet<Rue>());
        }


    }

    public Localisation[] determinerZoneInondee(long[] idsOrigin,double epsilon) {
        Set<Localisation> visited = new HashSet<>();
        Queue<Localisation> queue = new LinkedList<>();
        List<Localisation> result = new ArrayList<>();

        for (long l : idsOrigin) {
            Localisation start = nodes.get(idsOrigin);
            queue.add(start);
            visited.add(start);
        }

        while (!queue.isEmpty()){
            Localisation current = queue.poll();
            result.add(current);
            for (Rue rue : localisationListMap.get(current)) {
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

            for (Rue rue : localisationListMap.get(current)) {
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
        return null ;
    }

    public Deque<Localisation> trouverCheminDEvacuationLePlusCourt(long idOrigin, long idEvacuation, double vVehicule, Map<Localisation,Double> tFlood) {
        //TODO
		return null ;
    }


}
