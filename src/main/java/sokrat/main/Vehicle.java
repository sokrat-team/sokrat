package sokrat.main;

import java.util.List;

public class Vehicle {

    private Position currentPosition;
    private List<Ride> rides;
    private Ride currentRide;

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
    }

    public void endRide(){
        this.rides.add(currentRide);
        this.currentRide = null;
    }

    public boolean available(){
        return currentRide == null;
    }




}
