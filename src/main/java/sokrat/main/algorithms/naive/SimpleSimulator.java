package sokrat.main.algorithms.naive;

import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sokrat.main.Main;
import sokrat.main.definition.Rules;
import sokrat.main.model.Position;
import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

        Position center = new Position(rules.getNbColumns()/2, rules.getNbRows()/2);
        Collections.sort(freeVehicles, (v1,v2) -> Integer.compare(v2.getCurrentPosition().distanceTo(center), v1.getCurrentPosition().distanceTo(center)));

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


    public void allocateRamdomizedStrategy(int i, long l) {
        setStrategy(new RandomizedNextAvailableAndDoableRideStrategy(i,l));
    }
    public void allocateNextLongStrategy(int i) {
        setStrategy(new TakeLongerInNextRidesStrategy(i));
    }
    public void allocateNextCompromiseStrategy(int i) {
        setStrategy(new TakeCmompromiseInNextRidesStrategy(i));
    }
    public void allocateStayCentricStrategy(int i, Position position) {
        setStrategy(new StayCentricStrategy(i, position));
    }
    public void allocateStrategyPool(int maxSkip, Position center, Rules rules, long seed) {
        setStrategy(new StrategyPoolStrategy(maxSkip, center, rules, seed));
    }

    public void allocateIAmCloserStrategy(int maxSkip) {
        setStrategy(new IAmCloserStrategy(maxSkip));
    }


    public void allocateIAmClosestStrategy(int maxSkip) {
        setStrategy(new IAmClosestStrategy(maxSkip));
    }


    public void allocateRideNotIsolatedStrategy(int i, Rules r) {
        setStrategy(new RideNotIsolatedStrategy(i, r));
    }



    public class StrategyPoolStrategy implements AffectationStrategy{


        HashMap<AffectationStrategy, Integer> weightedStrategies = new HashMap<>();

        public StrategyPoolStrategy(int maxSkip, Position center, Rules rules, long seed){
            weightedStrategies.put(new RideNotIsolatedStrategy(maxSkip, rules),1);
            weightedStrategies.put(new RandomizedNextAvailableAndDoableRideStrategy(maxSkip, seed),1);
            weightedStrategies.put(new TakeCmompromiseInNextRidesStrategy(maxSkip),1);
            weightedStrategies.put(new StayCentricStrategy(maxSkip, center),1);
            weightedStrategies.put(new TakeLongerInNextRidesStrategy(maxSkip),1);
            weightedStrategies.put(new DontGoTooFarStrategy(maxSkip),1);
            weightedStrategies.put(nextAvailableAndDoableRideStrategy,2);
            weightedStrategies.put(new IAmCloserStrategy(maxSkip),3);
        }

        @Override
        public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {
            HashMap<Ride, Integer> rideCounts = new HashMap<>();

            for(Map.Entry<AffectationStrategy,Integer> entry : weightedStrategies.entrySet()){
                Optional<Ride> ride = entry.getKey().giveRideTo(vehicle,step);
                if(ride.isPresent()){
                    if (rideCounts.containsKey(ride)){
                        int newCount=rideCounts.get(ride) + entry.getValue();
                        rideCounts.put(ride.get(), newCount);
                    }else{
                        rideCounts.put(ride.get(), entry.getValue());
                    }
                }
            }
            Ride result = null;
            int maxCount=0;
            for(Ride ride : rideCounts.keySet()){
                if(rideCounts.get(ride) > maxCount){
                    result = ride;
                    maxCount = rideCounts.get(ride);
                }
            }

            return Optional.ofNullable(result);

        }
    }

    public class RandomizedNextAvailableAndDoableRideStrategy implements AffectationStrategy{

        private final int maxSkipped;
        private final Random random;

        public RandomizedNextAvailableAndDoableRideStrategy(int maxSkipped, long randomSeed){
            this.maxSkipped = maxSkipped;
            random = new Random(randomSeed);
        }

        @Override
        public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {
            return unasssignedRides.stream()
                    .parallel()
                    .filter(r -> canDoFullRide(r,vehicle,step))
                    .skip(random.nextInt(maxSkipped+1))
                    .findFirst();
        }
    }

    public class IAmCloserStrategy implements AffectationStrategy{

        private final int maxSkipped;

        public IAmCloserStrategy( int maxSkipped){
            this.maxSkipped = maxSkipped;
        }

        @Override
        public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {

            List<Ride> ridesPool = unasssignedRides.stream()
                    .parallel()
                    .filter(r -> canDoFullRide(r, vehicle, step))
                    .limit(freeVehicles.size() + maxSkipped )
                    .collect(Collectors.toList());

            Ride selected = null;
            int minDistance = Integer.MAX_VALUE;

            for(Ride ride: ridesPool){
                int distance = calculateRealDistanceToRide(vehicle, ride);
                if(distance < minDistance){
                    selected = ride;
                    minDistance=distance;
                }

            }


            return Optional.ofNullable(selected);
        }
    }


    public class IAmClosestStrategy implements AffectationStrategy{

        private final int maxSkipped;

        public IAmClosestStrategy( int maxSkipped){
            this.maxSkipped = maxSkipped;
        }

        @Override
        public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {

            List<Ride> ridesPool = unasssignedRides.stream()
                    .parallel()
                    .filter(r -> canDoFullRide(r, vehicle, step))
                    .limit(freeVehicles.size() + maxSkipped )
                    .collect(Collectors.toList());

            Ride selected = null;
            int minDistance = Integer.MAX_VALUE;

            Queue<Ride> rides = new ArrayDeque<>();

            for(Ride ride: ridesPool){
                int distance = calculateRealDistanceToRide(vehicle, ride);
                if(distance < minDistance){
                    minDistance=distance;
                    if(anotherVehicleIsCloser(ride,distance)){
                        rides.add(ride);
                    }
                    else{
                        selected = ride;
                    }
                }
            }
            if (selected == null && !rides.isEmpty()){
                selected = rides.poll();
            }
            return Optional.ofNullable(selected);
        }

        private boolean anotherVehicleIsCloser(Ride ride,int distance) {
            for (Vehicle v : freeVehicles){
                if(calculateRealDistanceToRide(v,ride) < distance) {
                    return true;
                }
            }
            return false;
        }
    }

    private int calculateRealDistanceToRide(Vehicle vehicle, Ride ride) {
        int distance = vehicle.getCurrentPosition().distanceTo(ride.getFrom());
        if ( ride.getEarliestStart() > currentStep+distance) {
            distance = ride.getEarliestStart() - currentStep;
        }
        return distance;
    }


    public class TakeLongerInNextRidesStrategy implements AffectationStrategy{


        private final int maxSkipped;

        public TakeLongerInNextRidesStrategy(int maxSkipped){
            this.maxSkipped = maxSkipped;
        }

        @Override
        public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {
            List<Ride> choice = unasssignedRides.stream()
                    .parallel()
                    .filter(r -> canDoFullRide(r, vehicle, step))
                    .limit(maxSkipped)
                    .collect(Collectors.toList());

            Ride selectedRide = null;
            int length = 0;
            int distance = 0;
            for( Ride ride : choice){
                if (selectedRide == null || length < ride.getLength()) {
                    selectedRide = ride;
                    length = ride.getLength();
                }
            }

            return Optional.ofNullable(selectedRide);
        }
    }

    public class DontGoTooFarStrategy implements AffectationStrategy{


        private final int maxSkipped;

        public DontGoTooFarStrategy(int maxSkipped){
            this.maxSkipped = maxSkipped;
        }

        @Override
        public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {
            List<Ride> choice = unasssignedRides.stream()
                    .parallel()
                    .filter(r -> canDoFullRide(r, vehicle, step))
                    .limit(maxSkipped)
                    .collect(Collectors.toList());

            Ride selectedRide = null;
            int length = 0;
            int distance = 0;
            for( Ride ride : choice){
                if (selectedRide == null || length > vehicle.getCurrentPosition().distanceTo(ride.getFrom())) {
                    selectedRide = ride;
                    length = vehicle.getCurrentPosition().distanceTo(ride.getFrom());
                }
            }

            return Optional.ofNullable(selectedRide);
        }
    }

    public class StayCentricStrategy implements AffectationStrategy{


        private final int maxSkipped;
        private final Position center;

        public StayCentricStrategy(int maxSkipped, Position center){
            this.maxSkipped = maxSkipped;
            this.center = center;
        }

        @Override
        public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {
            List<Ride> choice = unasssignedRides.stream()
                    .parallel()
                    .filter(r -> canDoFullRide(r, vehicle, step))
                    .limit(maxSkipped)
                    .collect(Collectors.toList());

            Ride selectedRide = null;
            int distanceToCenter = 0;

            for( Ride ride : choice){
                int distance=ride.getTo().distanceTo(center);
                if (selectedRide == null || distance < distanceToCenter) {
                    selectedRide = ride;
                    distanceToCenter = distance;
                }
            }

            return Optional.ofNullable(selectedRide);
        }
    }

    public class RideNotIsolatedStrategy implements AffectationStrategy{


        private final int maxSkipped;
        Map<Ride, Integer> distances;

        public RideNotIsolatedStrategy(int maxSkipped, Rules rules){
            this.maxSkipped = maxSkipped;
            distances = rules.computeMinDistancesBetweenRides();
        }

        @Override
        public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {
            List<Ride> choice = unasssignedRides.stream()
                    .parallel()
                    .filter(r -> canDoFullRide(r, vehicle, step))
                    .limit(maxSkipped)
                    .collect(Collectors.toList());

            Ride selectedRide = null;
            int distanceToRides= 0;

            for( Ride ride : choice){
                int distance=distances.get(ride);
                if (selectedRide == null || distance < distanceToRides) {
                    selectedRide = ride;
                    distanceToRides = distance;
                }
            }

            return Optional.ofNullable(selectedRide);
        }
    }

    public class TakeCmompromiseInNextRidesStrategy implements AffectationStrategy{


        private final int maxSkipped;

        public TakeCmompromiseInNextRidesStrategy(int maxSkipped){
            this.maxSkipped = maxSkipped;
        }

        @Override
        public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {
            List<Ride> choice = unasssignedRides.stream()
                    .parallel()
                    .filter(r -> canDoFullRide(r, vehicle, step))
                    .limit(maxSkipped)
                    .collect(Collectors.toList());

            Ride selectedRide = null;
            double lengthOverCost = 0.0;

            int distance = 0;
            for( Ride ride : choice){
                double loc =  ride.getLength()*1.0d / Math.max(vehicle.getCurrentPosition().distanceTo(ride.getFrom())+currentStep, ride.getEarliestStart());
                if (selectedRide == null || lengthOverCost < loc) {
                    selectedRide = ride;
                    lengthOverCost = loc;
                }
            }

            return Optional.ofNullable(selectedRide);
        }
    }

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
