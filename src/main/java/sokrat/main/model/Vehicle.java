package sokrat.main.model;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {

    private Position currentPosition;
    private List<Ride> rides=new ArrayList<>();
    private Ride currentRide;
    private Status status;
    private int timeAtTopOfRideStack;



    public void checkRide(int step) {
        switch(status){
            case MOVING_TO_RIDE_START:
                if (atStartingPoint()) moveToDestinationIfNeeded(step);
                break;
            case MOVING_TO_DESTINATION:
                if (atDestination()) endRide(step);
                break;

        }


    }


    private boolean atStartingPoint() {
        return currentPosition.equals(currentRide.getFrom());
    }

    private boolean atDestination() {
        return currentPosition.equals(currentRide.getTo());
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static enum Status{
        MOVING_TO_RIDE_START,
        MOVING_TO_DESTINATION,
        AVAILABLE
    }

    public Vehicle(Position initialPosition){
         currentPosition = initialPosition;
         status=Status.AVAILABLE;
        timeAtTopOfRideStack = 0;
    }

    public int getTimeAtTopOfRideStack(){
        timeAtTopOfRideStack = 0;
        Position currPos = new Position(0,0);
        for(Ride ride : rides){
            timeAtTopOfRideStack += currPos.distanceTo(ride.getFrom());
            currPos = ride.getFrom();
            timeAtTopOfRideStack += currPos.distanceTo(ride.getTo());
            currPos = ride.getTo();
        }

        return timeAtTopOfRideStack;
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

    public void goForRide(Ride newRide, int step) {
        this.currentRide = newRide;
        moveToRideStartIfNeeded(step);
    }

    private void moveToRideStartIfNeeded(int step) {
        if( currentPosition.equals(currentRide.getFrom()))
            moveToDestinationIfNeeded(step);
        else
            setStatus(Status.MOVING_TO_RIDE_START);

    }

    private void moveToDestinationIfNeeded( int step) {
        if(currentPosition.equals(currentRide.getTo()))
            setStatus(Status.AVAILABLE);
        else if(step >= currentRide.getEarliestStart()) {
            currentRide.setActualStartTime(step);
            setStatus(Status.MOVING_TO_DESTINATION);
        }
    }

    public void endRide(int step){
        currentRide.setActualArrivalTime(step);
        this.rides.add(getCurrentRide());
        this.currentRide = null;
        setStatus(Status.AVAILABLE);
    }

    public boolean available(){
        return getStatus() == Status.AVAILABLE;
    }


    public Ride getCurrentRide() {
        return currentRide;
    }

    public void move(int step){
        switch(getStatus()){
            case MOVING_TO_RIDE_START:
                moveTowardsOrigin();
                break;
            case MOVING_TO_DESTINATION:
                moveTowardsDestination();
                break;
            default:
                // no move
        }
    }

    public void moveTowardsDestination() {
        this.setCurrentPosition(currentPosition.moveTowards(currentRide.getTo()));
    }

    public void moveTowardsOrigin() {
        this.setCurrentPosition(currentPosition.moveTowards(currentRide.getFrom()));
    }
}
