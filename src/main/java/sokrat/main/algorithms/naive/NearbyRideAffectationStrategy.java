package sokrat.main.algorithms.naive;

import com.google.common.collect.HashMultiset;
import sokrat.main.definition.Rules;
import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.util.HashMap;
import java.util.Optional;

public class NearbyRideAffectationStrategy implements AffectationStrategy{

    private final Simulator simulator;


    public NearbyRideAffectationStrategy(Rules r, Simulator s, int maxDistrictNb){
        this.simulator = s;
        int maxDistrictSize = District.maxSize(r.getNbRows(),r.getNbColumns());

    }

    @Override
    public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {

        return null;
    }
}
