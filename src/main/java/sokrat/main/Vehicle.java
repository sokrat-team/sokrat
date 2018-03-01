package sokrat.main;

import java.util.List;

public class Vehicle {

    private Position currentPosition;
    private List<Ride> rides;
    private Ride currentRide;

    public static enum Status{
        MOVING_TO_RIDE,
        MOVING_TO_DESTINATION,
        AVAILABLE
    }

    public Vehicle(Position initialPosition){
         currentPosition = initialPosition;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

    public void AffectRide(Ride newRide) {
        this.currentRide = newRide;
        if( currentPosition.equals(newRide.getFrom())) moveToDestination();
    }

    private void moveToDestination() {
        //if(currentPosition)
        //status = Status.MOVING_TO_DESTINATION;
    }

    public void endRide(){
        this.rides.add(getCurrentRide());
        this.currentRide = null;
    }

    public boolean available(){
        return getCurrentRide() == null;
    }


    public Ride getCurrentRide() {
        return currentRide;
    }

    public void moveTowardsDestination() {
        this.setCurrentPosition(currentPosition.moveTowards(currentRide.getTo()));
    }

    public void moveTowardsOrigin() {
        this.setCurrentPosition(currentPosition.moveTowards(currentRide.getFrom()));
    }
}
