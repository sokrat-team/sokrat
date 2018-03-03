package sokrat.main.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class RideTest {

    @Test
    public void testLatestStart(){
        Ride r = new Ride(Position.INITIAL_POSITION, new Position(2,2),1,10,0);

        assertEquals(5,r.getLatestStart());

    }

}