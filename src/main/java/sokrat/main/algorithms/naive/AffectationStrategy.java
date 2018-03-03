package sokrat.main.algorithms.naive;

import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.util.Optional;

public interface AffectationStrategy {

    public Optional<Ride> giveRideTo(Vehicle vehicle, int step);

}
