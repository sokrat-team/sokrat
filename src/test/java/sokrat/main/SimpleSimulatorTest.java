package sokrat.main;

import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleSimulatorTest {

    @Test
    public void testAvailableToVehicle() {
        Ride r = new Ride(Position.INITIAL_POSITION, new Position(1,0),1,3,0);
        Vehicle v = new Vehicle(Position.INITIAL_POSITION);

        assertTrue("Ride should be available",SimpleSimulator.availableToVehicle(r, v,0));
        assertFalse("Ride should not be available",SimpleSimulator.availableToVehicle(r, v,2));


    }


    @Test
    public void testTooLateForARide() {
        Ride r = new Ride(Position.INITIAL_POSITION, new Position(1,0),1,3,0);

        assertTrue("Ride should be too late",SimpleSimulator.tooLateForARide(r, 2));
        assertFalse("Ride should be OK",SimpleSimulator.tooLateForARide(r, 0));

    }

    @Test
    public void testShortestUsingVehicle() {
        // TODO
    }
}