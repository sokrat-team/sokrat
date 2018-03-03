package sokrat.main.algorithms.naive;

import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.util.Optional;

public class NearbyRideAffectationStrategy implements AffectationStrategy{

    private final Simulator simulator;

    public NearbyRideAffectationStrategy(Simulator s){
        this.simulator = s;
    }

    @Override
    public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {

        return null;
    }
}
