package sokrat.main.definition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sokrat.main.model.Ride;

import java.util.*;
import java.util.stream.Collectors;

public class Rules {
    final int nbRows;
    final int nbColumns;
    final int duration;
    final int nbVehicles;
    private List<Ride> rides = new ArrayList<Ride>();
    final int bonus;


    static final Logger logger = LoggerFactory.getLogger(Rules.class);

    public Rules(int nbRows, int nbColumns, int duration, int nbVehicles, int bonus) {
        this.nbRows = nbRows;
        this.nbColumns = nbColumns;
        this.duration = duration;
        this.nbVehicles = nbVehicles;
        this.bonus = bonus;
    }

    public int getNbRows() {
        return nbRows;
    }

    public int getNbColumns() {
        return nbColumns;
    }

    public int getDuration() {
        return duration;
    }

    public int getNbVehicles() {
        return nbVehicles;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public int getBonus() {
        return bonus;
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

    public int getMaxPoints(){
        return getRides().stream().mapToInt(r->r.getLength()).sum() + getRides().size()*bonus;

    }

    public Rules eliminateShortestRides(double eliminatedRatio) {


        Rules results = new Rules(nbRows,nbColumns,duration,nbVehicles,bonus);

        int skipNumber = (int) Math.ceil(getRides().size() * eliminatedRatio);

        logger.info("removing {} shortest rides", skipNumber);

        results.setRides( rides.stream().sorted((r1,r2)->Integer.compare(r1.getLength(), r2.getLength())).skip(skipNumber).collect(Collectors.toList()) );

        return results;

    }


    public Rules eliminateFarthestRides(double eliminatedRatio){
        Map<Ride, Integer> ridesDistanceToOthers = computeMinDistancesBetweenRides();

        Rules results = new Rules(nbRows,nbColumns,duration,nbVehicles,bonus);

        int skipNumber = (int) Math.ceil(getRides().size() * eliminatedRatio);

        logger.info("removing {} farthest rides", skipNumber);

        results.setRides( rides.stream().sorted((r1,r2)->Integer.compare(ridesDistanceToOthers.get(r2), ridesDistanceToOthers.get(r1))).skip(skipNumber).collect(Collectors.toList()) );

        return results;
    }

    public Map<Ride, Integer> computeMinDistancesBetweenRides() {
        List<Ride> myRides = new ArrayList<>();
        myRides.addAll(rides);
        HashMap<Ride,Integer> ridesDistanceToOthers = new HashMap<>();

        for (Ride r1: rides){
            myRides.remove(r1);
            Optional<Integer> minDistanceToOtherRides = myRides.stream()
                    .map(r2 -> {if (r1.canBeBefore(r2)) return r1.distanceBetween(r2); else return r2.distanceBetween(r1);})
                    .min(Integer::compareTo);
            ridesDistanceToOthers.put(r1,minDistanceToOtherRides.get());
            myRides.add(r1);
        }
        return ridesDistanceToOthers;
    }

    public Ride getRidesByID(int id) {
        for(Ride r : rides){
            if (r.getIndex() == id) return r;
        }
        return null;
    }
}