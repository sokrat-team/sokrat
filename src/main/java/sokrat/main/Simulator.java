package sokrat.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Simulator {

    private final int nbRows;
    private final int nbColumns;
    private final int duration;
    private final int nbVehicles;
    private List<Ride> rides = new ArrayList<>();
    protected final Set<Vehicle> freeVehicles = new HashSet<>();
    protected final Set<Vehicle> busyVehicles = new HashSet<>();
    private final int bonus;


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

    public Solution getSolution(){
        return Solution.generateSolution(getAllVehicles(),bonus );

    }

    private List<Vehicle> getAllVehicles() {
        ArrayList<Vehicle> results = new ArrayList<>();
        results.addAll(busyVehicles);
        results.addAll(freeVehicles);
        return results;
    }
    protected final List<Ride> unasssignedRides = new ArrayList<>();

    public void addRides(List<Ride> rides) {
        this.rides = rides;
        unasssignedRides.addAll(rides);
        unasssignedRides.sort((r1,r2)->compareRides(r1,r2));
    }

    protected int compareRides(Ride r1, Ride r2){
        int results = Integer.compare(r1.getEarliestStart(),r2.getEarliestStart());
        if(results == 0 ) return Integer.compare(r1.getLength(),r2.getLength());
        return results;
    }

    public int getBonus() {
        return bonus;
    }

}
