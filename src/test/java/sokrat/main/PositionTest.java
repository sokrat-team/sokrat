package sokrat.main;

import org.junit.Test;

import static org.junit.Assert.*;

public class PositionTest {

    @Test
    public void testCompare(){
        assertEquals(0, Position.INITIAL_POSITION.compareTo(new Position(0,0)));

    }

}