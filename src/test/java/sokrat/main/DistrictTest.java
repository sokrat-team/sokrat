package sokrat.main;

import org.junit.Test;

import static org.junit.Assert.*;

public class DistrictTest {

    @Test
    public void testDistrictNumber() {
        District instance = new District(10,10,2);
        assertEquals(1,instance.districtNumber(Position.INITIAL_POSITION));
        assertEquals(1,instance.districtNumber(new Position(1,0)));
        assertEquals(1,instance.districtNumber(new Position(0,1)));
        assertEquals(2,instance.districtNumber(new Position(2,0)));
        assertEquals(6,instance.districtNumber(new Position(1,2)));

    }
}