package sokrat.main;

import com.google.common.collect.Lists;
import org.junit.Test;
import sokrat.main.algorithms.Solution;
import sokrat.main.definition.Parser;
import sokrat.main.definition.ParserException;
import sokrat.main.definition.Rules;
import sokrat.main.model.Position;
import sokrat.main.model.Ride;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SolutionLoaderTest {

    @Test
    public void loadSolution() throws IOException, ParserException {
        Parser p = new Parser(new StringReader("3 4 2 3 2 10\n" +
                "0 0 1 3 2 9\n" +
                "1 2 1 0 0 9\n" +
                "2 0 2 2 0 9\n"));
        Rules rules = p.getRules();

        Solution s = SolutionLoader.loadSolution(new StringReader("1 0\n2 1 2"), "test", rules);

        assertEquals("1 0\n2 1 2", s.toString().trim());

        assertEquals(10, s.gain());

    }
}