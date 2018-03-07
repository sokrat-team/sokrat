package sokrat.main.definition;

import org.junit.Test;
import sokrat.main.algorithms.naive.SimpleSimulator;
import sokrat.main.algorithms.naive.Simulator;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static sokrat.main.algorithms.naive.RidesOrderingStrategy.DEFAULT;

public class RulesAnalyzerTest {

    @Test
    public void testAnalyze() throws IOException, ParserException {
        File f = new File("input_files/d_metropolis.in");
        Rules rules = new Parser(f).getRules();

        System.out.println(new RulesAnalyzer(rules, new int[]{50,100,200,400,800,1600}).toString());
    }

    @Test
    public void testAnalyze2() throws IOException, ParserException {
        File f = new File("input_files/d_metropolis.in");
        Rules rules = new Parser(f).getRules();

        System.out.println(new RulesAnalyzer(rules, new int[]{600}).toString());
    }
}