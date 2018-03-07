package sokrat.main.definition;

import sokrat.main.model.Ride;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Rules {
    final int nbRows;
    final int nbColumns;
    final int duration;
    final int nbVehicles;
    private List<Ride> rides = new ArrayList<Ride>();
    final int bonus;



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

        results.setRides( rides.stream().sorted((r1,r2)->Integer.compare(r1.getLength(), r2.getLength())).skip(skipNumber).collect(Collectors.toList()) );

        return results;

    }

    public Ride getRidesByID(int id) {
        for(Ride r : rides){
            if (r.getIndex() == id) return r;
        }
        return null;
    }
}