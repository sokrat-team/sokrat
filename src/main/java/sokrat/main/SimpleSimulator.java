package sokrat.main;

import java.util.List;
import java.util.Set;

public class SimpleSimulator extends Simulator{


    private int nbRemainingSteps;

    public SimpleSimulator(int duration, int nbRows, int nbColumns, int nbVehicles,int bonus) {
        super(duration, nbRows, nbColumns, nbVehicles,bonus);
    }

    public SimpleSimulator(int duration, int nbRows, int nbColumns, int nbVehicles, List<Ride> rides, int bonus) {
        super(duration, nbRows, nbColumns, nbVehicles, bonus);
        super.setRides(rides);
    }

    @Override
    public int runSimulation() {
        this.initialize();
        for (nbRemainingSteps = getDuration(); nbRemainingSteps > 0 ; nbRemainingSteps--  ){
            this.affectRides();
            this.moveVehicles();
            this.checkVehicles();
        }
        return calculateScore();
    }

    private int calculateScore() {
        return 0;
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    private void moveVehicles() {
        for(Vehicle vehicle: busyVehicles){
            vehicle.moveTowardsDestination();
        }
    }

    private void checkVehicles() {
    }

    public void affectRides(){
        
    }

}
