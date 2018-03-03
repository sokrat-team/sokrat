package sokrat.main.model;

import org.junit.Test;
import sokrat.main.model.Position;
import sokrat.main.algorithms.naive.District;

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
        assertEquals(25,instance.districtNumber(new Position(9,9)));
        assertEquals(25,instance.districtNumber(new Position(8,9)));
        assertEquals(25,instance.districtNumber(new Position(8,8)));
        assertEquals(25,instance.districtNumber(new Position(9,8)));
        assertEquals(24,instance.districtNumber(new Position(7,8)));
        assertEquals(20,instance.districtNumber(new Position(8,7)));

    }

    @Test
    public void testSameDistrict() {
        District instance = new District(10,10,2);
        assertTrue("should be same district",instance.sameDistrict(Position.INITIAL_POSITION,new Position(1,0)));
        assertFalse("should not be same district",instance.sameDistrict(Position.INITIAL_POSITION,new Position(1,2)));
    }
}