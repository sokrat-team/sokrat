package sokrat.main;

import java.util.Set;

public class SimpleSimulator extends Simulator{


    private int nbRemainingSteps;

    public SimpleSimulator(int duration, int nbRows, int nbColumns, int nbVehicles, Set<Ride> rides) {
        super(duration, nbRows, nbColumns, nbVehicles, rides);
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

    }

    private void moveVehicles() {

    }

    private void checkVehicles() {
    }

    public void affectRides(){
        
    }

}
