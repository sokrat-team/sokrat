package sokrat.main;

import java.util.ArrayList;
import java.util.List;

public class SimpleSimulator extends Simulator{



    private int currentStep;



    public SimpleSimulator(int duration, int nbRows, int nbColumns, int nbVehicles,int bonus) {
        super(duration, nbRows, nbColumns, nbVehicles,bonus);
        initializeStrategy();
    }

    private AffectationStrategy strategy;

    public SimpleSimulator(int duration, int nbRows, int nbColumns, int nbVehicles, List<Ride> rides, int bonus) {
        super(duration, nbRows, nbColumns, nbVehicles, bonus);
        initializeStrategy();
        addRides(rides);
    }

    private void initializeStrategy() {
        setStrategy(nextAvailableAndDoableRideStrategy);
    }




    @Override
    public int runSimulation() {
        this.initialize();

        for (currentStep = 0; currentStep < getDuration() ; currentStep++  ){
            this.cleanUndoableRides(currentStep);
            this.affectRides(currentStep);
            this.moveVehicles(currentStep);
            this.checkVehicles(currentStep);
        }
        return calculateScore();
    }

    private void cleanUndoableRides(int step) {
        unasssignedRides.removeIf(r-> tooLateForARide(r, currentStep));
    }

    public static boolean tooLateForARide(Ride r, int currentStep) {
        return (currentStep + r.getLength()) >= r.getLatestFinish();
    }

    private int calculateScore() {
        return getSolution().gain();
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    private void moveVehicles(int currentStep) {
        for(Vehicle vehicle: busyVehicles){
            vehicle.move(currentStep);
        }
    }

    private void checkVehicles(int currentStep) {
        List<Vehicle> newFreeVehicles = new ArrayList<>();
        for(Vehicle v : busyVehicles){
            v.checkRide(currentStep);
            if(v.available()){
                newFreeVehicles.add(v);
            }

        }
        busyVehicles.removeAll(newFreeVehicles);
        freeVehicles.addAll(newFreeVehicles);

    }

    private void affectRides(int currentStep){

        for(Vehicle vehicle : freeVehicles){
            affectRideTo(vehicle, currentStep);
        }
        busyVehicles.addAll(freeVehicles);
        freeVehicles.clear();

    }

    private void affectRideTo(Vehicle vehicle, int step) {
        if(unasssignedRides.isEmpty()) return;
        getStrategy().giveRideTo(vehicle,step);

    }




    private AffectationStrategy nextAvailableRideStrategy = (vehicle, step) -> unasssignedRides.stream()
            .findFirst()
            .ifPresent( r -> affectRideTo(r,vehicle, step));


    private AffectationStrategy nextAvailableAndDoableRideStrategy = (vehicle, step) -> unasssignedRides.stream()
            .parallel()
            .filter(r -> availableToVehicle(r,vehicle,step))
            .findFirst()
            .ifPresent( r -> affectRideTo(r,vehicle, step));


    private void affectRideTo(Ride r, Vehicle vehicle, int currentStep) {
        unasssignedRides.remove(r);
        vehicle.AffectRide(r, currentStep);
    }



    public static boolean availableToVehicle(Ride r, Vehicle vehicle, int step) {
        int canStartStep = step + r.getFrom().distanceTo(vehicle.getCurrentPosition());
        int earliestStart = Math.max(r.getEarliestStart(), canStartStep);
        return  r.getLatestFinish() > (earliestStart + r.getLength());
    }

    public static int shortestUsingVehicle(Ride r1, Ride r2, Vehicle vehicle) {
        return Integer.compare(r1.getFrom().distanceTo(vehicle.getCurrentPosition()),
                r2.getFrom().distanceTo(vehicle.getCurrentPosition()));
    }

    public AffectationStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(AffectationStrategy strategy) {
        this.strategy = strategy;
    }
}
