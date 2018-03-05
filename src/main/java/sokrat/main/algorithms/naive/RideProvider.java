package sokrat.main.algorithms.naive;

import sokrat.main.model.Ride;

import java.util.List;

public interface RideProvider {

    public List<Ride> getRemainingRides();
}
