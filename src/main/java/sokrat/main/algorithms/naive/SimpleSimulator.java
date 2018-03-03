package sokrat.main.algorithms.naive;

import sokrat.main.definition.Rules;
import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class SimpleSimulator extends Simulator{



    private int currentStep;



    public SimpleSimulator(Rules rules, RidesOrderingStrategy orderingStrategy) {
        super(rules,orderingStrategy);
        initializeStrategy();
    }

    private AffectationStrategy strategy;

    public SimpleSimulator(Rules rules, AffectationStrategy strategy, RidesOrderingStrategy orderingStrategy) {
        super(rules,orderingStrategy);
        setStrategy(strategy);
    }

    private void initializeStrategy() {
        setStrategy(nextAvailableAndDoableRideStrategy);
    }




    @Override
    public int runSimulation() {
        this.initialize();

        for (currentStep = 0; currentStep < rules.getDuration(); currentStep++  ){
            this.cleanUndoableRides(currentStep);
            this.affectRides(currentStep);
            this.moveVehicles(currentStep);
            this.checkVehicles(currentStep);
        }
        return calculateScore();
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

    void cleanUndoableRides(int step) {
        unasssignedRides.removeIf(r-> tooLateForARide(r, currentStep));
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
        strategy.giveRideTo(vehicle,step).ifPresent( r -> affectRideTo(r,vehicle, step));
    }



    private AffectationStrategy nextAvailableRideStrategy = (vehicle, step) -> unasssignedRides.stream()
            .findFirst();


    private AffectationStrategy nextAvailableAndDoableRideStrategy = (vehicle, step) -> unasssignedRides.stream()
            .parallel()
            .filter(r -> availableToVehicle(r,vehicle,step))
            .findFirst();


    private void affectRideTo(Ride r, Vehicle vehicle, int currentStep) {
        unasssignedRides.remove(r);
        vehicle.AffectRide(r, currentStep);
    }



    public static boolean availableToVehicle(Ride r, Vehicle vehicle, int step) {
        int canStartStep = step + r.getFrom().distanceTo(vehicle.getCurrentPosition());

        return  canStartStep <= r.getLatestStart();
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
