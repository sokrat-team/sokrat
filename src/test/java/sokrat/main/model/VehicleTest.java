package sokrat.main.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class VehicleTest {

    @Test
    public void testVehicleShouldGoToDestinationIfAlreadyAtStart(){
        Vehicle v = new Vehicle(Position.INITIAL_POSITION);
        v.goForRide(new Ride(new Position(0,0), new Position(1,0),0,1,0),1);
        assertEquals(Vehicle.Status.MOVING_TO_DESTINATION, v.getStatus() );

    }

    @Test
    public void testVehicleAvailableIfRideIsEmpty(){
        Vehicle v = new Vehicle(Position.INITIAL_POSITION);
        v.goForRide(new Ride(new Position(0,0), new Position(0,0),0,1,0),1);
        assertEquals(Vehicle.Status.AVAILABLE, v.getStatus() );

    }

    @Test
    public void testVehicleMoveToRide(){
        Vehicle v = new Vehicle(Position.INITIAL_POSITION);
        v.goForRide(new Ride(new Position(1,0), new Position(1,2),0,10,0),1);
        assertEquals(Vehicle.Status.MOVING_TO_RIDE_START, v.getStatus() );
        v.move(0);
        v.checkRide(0);
        assertEquals(Vehicle.Status.MOVING_TO_DESTINATION, v.getStatus() );
        v.move(1);
        v.checkRide(1);
        assertEquals(Vehicle.Status.MOVING_TO_DESTINATION, v.getStatus() );
        v.move(2);
        v.checkRide(2);
        assertEquals(Vehicle.Status.AVAILABLE, v.getStatus() );
        assertEquals(1, v.getRides().size());

    }

}