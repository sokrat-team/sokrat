package sokrat.main;

import java.util.List;

public class Vehicle {

    private Position currentPosition;
    private List<Ride> affectedRides;

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    public List<Ride> getAffectedRides() {
        return affectedRides;
    }

    public void setAffectedRides(List<Ride> affectedRides) {
        this.affectedRides = affectedRides;
    }

    public void AffectRide(Ride newRide) {
        this.affectedRides.add(newRide);
    }
}
