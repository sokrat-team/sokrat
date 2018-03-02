package sokrat.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class SimpleSimulator extends Simulator{



    private int currentStep;

    public SimpleSimulator(int duration, int nbRows, int nbColumns, int nbVehicles,int bonus) {
        super(duration, nbRows, nbColumns, nbVehicles,bonus);
    }

    public SimpleSimulator(int duration, int nbRows, int nbColumns, int nbVehicles, List<Ride> rides, int bonus) {
        super(duration, nbRows, nbColumns, nbVehicles, bonus);
        addRides(rides);
    }


    @Override
    public int runSimulation() {
        this.initialize();

        for (currentStep = 0; currentStep < getDuration() ; currentStep++  ){
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

    private void affectRides(int currentStep){

        for(Vehicle vehicle : freeVehicles){
            affectRideTo(vehicle, currentStep);
        }
        busyVehicles.addAll(freeVehicles);
        freeVehicles.clear();

    }

    private void affectRideTo(Vehicle vehicle, int step) {
        if(unasssignedRides.isEmpty()) return;
            unasssignedRides.stream()
                .findFirst()
                .ifPresent( r -> affectRideTo(r,vehicle, step));
        }

    private void affectRideTo(Ride r, Vehicle vehicle, int currentStep) {
        //System.out.println("Ride "  + r.getIndex() + " to " + vehicle);
        unasssignedRides.remove(r);
        vehicle.AffectRide(r, currentStep);

    }



    private boolean availableToVehicle(Ride r, Vehicle vehicle, int step) {
        int startStep = step + r.getFrom().distanceTo(vehicle.getCurrentPosition());
        int earliestStart = Math.max(r.getEarliestStart(), startStep);
        return r.getEarliestStart() <= startStep &&
                r.getLatestFinish() <= earliestStart + r.getLength();
    }

    private int shortestUsingVehicle(Ride r1, Ride r2, Vehicle vehicle) {
        return Integer.compare(r1.getFrom().distanceTo(vehicle.getCurrentPosition()),
                r2.getFrom().distanceTo(vehicle.getCurrentPosition()));
    }

}
