package sokrat.main.algorithms.naive;

import com.google.common.collect.TreeMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sokrat.main.Main;
import sokrat.main.definition.Rules;
import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.util.*;

public class NearbyRideAffectationStrategy implements AffectationStrategy{

    private final RidesOrderingStrategy ordering;
    private final int maxDistrictNb;
    private final Rules rules;
    private final int maxDistrictSize;
    private List<Integer> districtSizesOrdered;
    ArrayList<Ride> allRemainingRides = new ArrayList<>();

    HashMap<Integer, DistrictPool> districtsBySize = new HashMap<>();

    static final Logger logger = LoggerFactory.getLogger(Main.class);
    private int lastStep=-1;


    private class DistrictPool{
        District district;

        DistrictPool(int size){
            district = new District(rules.getNbRows(), rules.getNbColumns(), size);
            rules.getRides().forEach(r->ridesForDistrict.put(district.districtNumber(r.getFrom()), r));
        }

        TreeMultimap<Integer, Ride> ridesForDistrict = TreeMultimap.create(Integer::compareTo, ordering);

        public void removeRideFromPool(Ride r){
            ridesForDistrict.remove(district.districtNumber(r.getFrom()), r);
        }

        public Optional<Ride> nextAvailableRideForVehicle(Vehicle v, int step){
            int currentDistrict = district.districtNumber(v.getCurrentPosition());

            if (ridesForDistrict.containsKey(currentDistrict)){
                Optional<Ride> r = ridesForDistrict.get(currentDistrict).stream().filter(rd->Simulator.canDoFullRide(rd,v,step)).findFirst();
                if(r.isPresent()) {
                    ridesForDistrict.remove(currentDistrict, r.get());
                    return r;
                }
            }
            return Optional.empty();
        }

        public boolean isEmpty(){
            return ridesForDistrict.isEmpty();
        }
    }

    public NearbyRideAffectationStrategy(Rules r, int maxDistrictPools, RidesOrderingStrategy ordering){
        this.rules =r ;
        this.ordering = ordering;
        this.maxDistrictNb = maxDistrictPools;
        this.maxDistrictSize = District.maxSize(r.getNbRows(),r.getNbColumns());
        allRemainingRides.addAll(r.getRides());
        allRemainingRides.sort(RidesOrderingStrategy.LATEST_START_LAST);
        createDistrictPools();

    }

    private void createDistrictPools() {
        int nbPools = 0;
        int size = maxDistrictSize;
        int sizeOffest = Math.max(maxDistrictSize/maxDistrictNb,1);
        for(; size > sizeOffest && nbPools < maxDistrictNb; size-=sizeOffest){
            createDistrictPool(size);
            nbPools++;
        }

        districtSizesOrdered = new ArrayList<>();
        districtSizesOrdered.addAll(districtsBySize.keySet());
        districtSizesOrdered.sort(Integer::compareTo);
        logger.info("Created {} district pools with size between {} and {} with offset {}",nbPools, size,maxDistrictSize, sizeOffest);

    }

    private void createDistrictPool(int size) {
        districtsBySize.put(size,new DistrictPool(size));
    }

    public void removeRideFromPools(Ride r){

        for(DistrictPool pool : districtsBySize.values()){
            pool.removeRideFromPool(r);
        }
    }



    @Override
    public Optional<Ride> giveRideTo(Vehicle vehicle, int step) {
        if (lastStep < step) {
            cleanUndoableRides(step);
            cleanEmptyPools();
            lastStep = step;
        }

        Optional<Ride> val;
        for(int s : districtSizesOrdered){

            val= districtsBySize.get(s).nextAvailableRideForVehicle(vehicle, step);

            if(val.isPresent()) {
                removeRideFromPools(val.get());
                return val;
            }

        }
        return Optional.empty();
    }

    private void cleanEmptyPools() {
        ArrayList<Integer> toRemove = new ArrayList<>();
        for(Integer dSize : districtSizesOrdered){
            DistrictPool pool = districtsBySize.get(dSize);
            if(pool.isEmpty()){
                districtsBySize.remove(dSize);
                toRemove.add(dSize);
            }

        }
        toRemove.forEach( i -> districtSizesOrdered.remove(i));
    }

    private void cleanUndoableRides(int step) {
        ArrayList<Ride> removed = new ArrayList<>();
        for(Ride r : allRemainingRides) {
            if(Simulator.tooLateForARide(r,step)){
                removeRideFromPools(r);
                removed.add(r);
            }else{
                break;
            }
        }
        allRemainingRides.removeAll(removed);
    }


}
