package sokrat.main.algorithms.naive;

import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static sokrat.main.algorithms.naive.Simulator.canDoFullRide;

public class ShortestPathToRidesStrategy implements AffectationStrategy {


    RideProvider provider;

    public ShortestPathToRidesStrategy(RideProvider p, NextRideFinder strategy){
        this.provider = p;
    }

    public ShortestPathToRidesStrategy(NextRideFinder strategy){
        this.strategy = strategy;
    }

    public RideProvider getProvider() {
        return provider;
    }

    public void setProvider(RideProvider provider) {
        this.provider = provider;
    }

    @Override
    public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {
        List<Ride> prevRides = vehicle.getRides();

        if(prevRides.isEmpty())
            return provider.getRemainingRides().stream().findFirst();

        Ride r = prevRides.get(prevRides.size()-1);
        return strategy.nextRide(provider, vehicle,step, r);
        //return


    }

    public static interface NextRideFinder{
        public Optional<Ride> nextRide(RideProvider provider, Vehicle vehicle, int step, Ride r );
    }



    public static NextRideFinder DEFAULT_STRATEGY = (RideProvider provider, Vehicle vehicle, int step, Ride r ) -> provider.getRemainingRides().stream()
            .parallel()
            .filter(ride -> canDoFullRide(ride,vehicle,step+r.getLength()))
            .sorted((r1, r2) -> Double.compare(r.TimeLostToNextEarlyStart(r1,step), r.TimeLostToNextEarlyStart(r2,step)))
            .findFirst();
    public static NextRideFinder AVG_DISTANCE_STRATEGY = (RideProvider provider, Vehicle vehicle, int step, Ride r ) -> provider.getRemainingRides().stream()
            .parallel()
            .filter(ride -> canDoFullRide(ride,vehicle,step+r.getLength()))
            .sorted((r1, r2) -> Double.compare(r.avgLostTimeBetween(r1), r.avgLostTimeBetween(r2)))
            .findFirst();
    public static NextRideFinder LATEST_DISTANCE_STRATEGY = (RideProvider provider, Vehicle vehicle, int step, Ride r ) -> provider.getRemainingRides().stream()
            .parallel()
            .filter(ride -> canDoFullRide(ride,vehicle,step+r.getLength()))
            .sorted((r1, r2) -> Double.compare(r.TimeLostToNextEarlyStart(r1,step), r.TimeLostToNextEarlyStart(r2,step)))
            .findFirst();


    private NextRideFinder strategy=DEFAULT_STRATEGY;







}
