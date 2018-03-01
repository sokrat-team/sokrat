package sokrat.main;

import java.util.HashSet;
import java.util.Set;

public abstract class Simulator {

    private final int nbRows;
    private final int nbColumns;
    private final int duration;
    private final int nbVehicles;
    private final Set<Ride> rides = new HashSet<>();
    private final Set<Vehicle> vehicles = new HashSet<>();

    private int score = 0;

    public Simulator(int duration, int nbRows, int nbColumns, int nbVehicles, Set<Ride> rides ){
        this.duration = duration;
        this.nbVehicles = nbVehicles;
        this.nbRows = nbRows;
        this.nbColumns = nbColumns;
        
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

    public Set<Ride> getRides() {
        return rides;
    }

    public abstract int runSimulation();

    public void initialize(){
        for(int i = 0 ; i<nbVehicles; i++){
            vehicles.add(new Vehicle(Position.INITIAL_POSITION));
        }

    }

}
