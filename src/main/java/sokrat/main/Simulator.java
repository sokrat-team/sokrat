package sokrat.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Simulator {

    private final int nbRows;
    private final int nbColumns;
    private final int duration;
    private final int nbVehicles;
    private List<Ride> rides = new ArrayList<>();
    protected final Set<Vehicle> freeVehicles = new HashSet<>();
    protected final Set<Vehicle> busyVehicles = new HashSet<>();
    private final int bonus;

    private int score = 0;

    public Simulator(int duration, int nbRows, int nbColumns, int nbVehicles, int bonus ){
        this.duration = duration;
        this.nbVehicles = nbVehicles;
        this.nbRows = nbRows;
        this.nbColumns = nbColumns;
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

    public abstract int runSimulation();

    public void initialize(){
        for(int i = 0 ; i<nbVehicles; i++){
            freeVehicles.add(new Vehicle(Position.INITIAL_POSITION));
        }

    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

    public int getBonus() {
        return bonus;
    }
}
