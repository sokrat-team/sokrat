package sokrat.main.algorithms.naive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sokrat.main.Main;
import sokrat.main.definition.Rules;
import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.util.ArrayList;

public class ByVehicleSimulator extends Simulator {
    static final Logger logger = LoggerFactory.getLogger(Main.class);


    public ByVehicleSimulator(Rules r) {
        super(r, RidesOrderingStrategy.EARLIEST_START_FIRST);
    }

    @Override
    public int runSimulation() {
        for (Vehicle v : getAllVehicles()){
            doVehicle(v);
        }
        return calculateScore();
    }

    private void doVehicle(Vehicle v) {

        int firstDoableIndex = -1;

        int timer = 0;
        ArrayList<Ride> toRemove = new ArrayList<>();
        for(int currentIndex=0;currentIndex<unasssignedRides.size() && timer < rules.getDuration();currentIndex++){

            Ride r = unasssignedRides.get(currentIndex);
            Ride goForIt = null;
            if (Simulator.canDoFullRide(r,v,timer)) {
                if( Simulator.justInTimeForDeparture(r,v,timer)){
                    goForIt =r;
                }else{
                    if (firstDoableIndex < 0) firstDoableIndex=currentIndex ;
                }
            }else if( firstDoableIndex >= 0) {
                goForIt = unasssignedRides.get(firstDoableIndex);
            }
            if( currentIndex==unasssignedRides.size()-1 && goForIt==null && firstDoableIndex>=0){
                goForIt = unasssignedRides.get(firstDoableIndex);
                currentIndex = firstDoableIndex +1;
                firstDoableIndex = -1;
            }
            if(goForIt != null){
                goForARide(v,goForIt,timer);
                timer = move(v,goForIt,timer);
                unasssignedRides.remove(goForIt);
            }

        }
    }

    private int move(Vehicle v, Ride r, int timer) {
        v.setCurrentPosition(r.getTo());
        timer += v.getCurrentPosition().distanceTo(r.getFrom());
        timer = Math.max(timer,r.getEarliestStart());
        r.setActualStartTime(timer);
        timer += r.getLength();
        r.setActualArrivalTime(timer);
        v.endRide(timer);
        return timer;
    }

    private void goForARide(Vehicle v, Ride r, int step) {
        v.goForRide(r,step);
    }
}
