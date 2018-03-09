package sokrat.main.algorithms.naive;

import sokrat.main.algorithms.Solution;
import sokrat.main.definition.Rules;
import sokrat.main.model.Position;
import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.util.*;

public abstract class Simulator {

    private final RidesOrderingStrategy orderingStrategy;
    protected Rules rules;
    protected final List<Vehicle> freeVehicles = new ArrayList<>();
    protected final Set<Vehicle> busyVehicles = new HashSet<>();


    public Simulator(Rules r, RidesOrderingStrategy orderingStrategy ){
        this.rules = r;
        this.orderingStrategy = orderingStrategy;
        initialize();
        addRides(r.getRides());

    }


    public abstract int runSimulation();

    public void initialize(){
        for(int i = 0; i< rules.getNbVehicles(); i++){
            freeVehicles.add(new Vehicle(Position.INITIAL_POSITION));
        }
    }

    public Solution getSolution(){
        return Solution.generateSolution(getAllVehicles(), rules.getBonus());

    }

    protected List<Vehicle> getAllVehicles() {
        ArrayList<Vehicle> results = new ArrayList<>();
        results.addAll(busyVehicles);
        results.addAll(freeVehicles);
        return results;
    }
    protected final List<Ride> unasssignedRides = new ArrayList<>();

    public void addRides(List<Ride> rides) {
        unasssignedRides.addAll(rides);
        unasssignedRides.sort(orderingStrategy);
    }



    public int getBonus() {
        return rules.getBonus();
    }

    public int calculateGain(){
        int gain = 0;
        for (Vehicle v : getAllVehicles()){
            for( Ride r : v.getRides()){
                if(r.finishedOnTime()) {
                    gain += r.getLength();
                    if (r.startedOnTime()) gain += rules.getBonus();
                }

            }

        }
        return gain;
    }


    public static boolean tooLateForARide(Ride r, int currentStep) {
        return (currentStep + r.getLength()) >= r.getLatestFinish();
    }

    public List<Ride> getUnasssignedRides() {
        return unasssignedRides;
    }

    public static boolean canDoFullRide(Ride r, Vehicle vehicle, int step) {
        int canStartStep = getCanStartStep(r, vehicle, step);

        return  canStartStep <= r.getLatestStart();
    }

    private static int getCanStartStep(Ride r, Vehicle vehicle, int step) {
        return step + r.getFrom().distanceTo(vehicle.getCurrentPosition());
    }

    public static int shortestUsingVehicle(Ride r1, Ride r2, Vehicle vehicle) {
        return Integer.compare(r1.getFrom().distanceTo(vehicle.getCurrentPosition()),
                r2.getFrom().distanceTo(vehicle.getCurrentPosition()));
    }

    public static boolean justInTimeForDeparture(Ride r, Vehicle v, int step) {
        return getCanStartStep(r,v,step) == r.getEarliestStart();
    }

    public int calculateScore() {
        return getSolution().gain();
    }
}
