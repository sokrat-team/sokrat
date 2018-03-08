package sokrat.main.algorithms.naive;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sokrat.main.Main;
import sokrat.main.definition.Rules;
import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

public class SimpleSimulator extends Simulator{


    static final Logger logger = LoggerFactory.getLogger(Main.class);

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


        Stopwatch timer = Stopwatch.createStarted();
        for (currentStep = 0; currentStep < rules.getDuration(); currentStep++  ){
            this.cleanUndoableRides(currentStep);
            this.affectRides(currentStep);
            this.moveVehicles(currentStep);
            this.checkVehicles(currentStep);
            if(timer.elapsed(TimeUnit.SECONDS) >= 10) {
                logger.info("step: {}/{} {}ms",currentStep,rules.getDuration(), NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
                timer.reset();
                timer.start();
            }
        }
        return calculateScore();
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
            .filter(r -> canDoFullRide(r,vehicle,step))
            .findFirst();


    private void affectRideTo(Ride r, Vehicle vehicle, int currentStep) {
        unasssignedRides.remove(r);
        vehicle.goForRide(r, currentStep);
    }





    public AffectationStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(AffectationStrategy strategy) {
        this.strategy = strategy;
    }


}
