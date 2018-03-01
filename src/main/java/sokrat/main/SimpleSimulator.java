package sokrat.main;

import java.util.List;

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
        for(Vehicle v : busyVehicles){
            v.checkRide(currentStep);
            if (v.available()){
               freeVehicles.add(v);
               busyVehicles.remove(v);
            }
        }
    }

    public void affectRides(int currentStep){
        for(Vehicle vehicle : freeVehicles){
            affectRideTo(vehicle);
        }
    }

    private void affectRideTo(Vehicle vehicle) {

    }

}
