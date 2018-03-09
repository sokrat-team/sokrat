package sokrat.main;

import org.junit.Test;
import sokrat.main.algorithms.Solution;
import sokrat.main.algorithms.naive.RidesOrderingStrategy;
import sokrat.main.algorithms.naive.SimpleSimulator;
import sokrat.main.definition.Parser;
import sokrat.main.algorithms.naive.Simulator;
import sokrat.main.definition.ParserException;
import sokrat.main.definition.Rules;
import sokrat.main.model.Position;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;
import static sokrat.main.Main.proceedNaive;
import static sokrat.main.Main.writeSolutionToFile;
import static sokrat.main.algorithms.naive.RidesOrderingStrategy.DEFAULT;

public class MainTest {

    @Test(expected=FileNotFoundException.class)
    public void testInvalidFileShouldFail() throws FileNotFoundException {
        new Main(new File("do not exists"),new File("do not exists either"));
    }

    @Test(expected=NullPointerException.class)
    public void testNullFileShouldFail() throws FileNotFoundException {
        new Main(null,new File("do not exists either"));
    }

    @Test
    public void testConstructorWithExistingFile() throws IOException {
        File f = new File("testFile");
        f.createNewFile();
        assertTrue(f.exists());
        new Main(f,new File("do not exists yet"));
        f.delete();

    }

    @Test
    public void testSimple() throws IOException, ParserException {
        File f = new File("input_files/a_example.in");
        Simulator s = new SimpleSimulator(new Parser(f).getRules(),DEFAULT);
        s.runSimulation();
        System.out.println(s.getSolution().toString());
    }

    public void testShouldBeEasy() throws IOException, ParserException {
        File f = new File("input_files/b_should_be_easy.in");
        Simulator s = new SimpleSimulator(new Parser(f).getRules(),DEFAULT);
        s.runSimulation();
        System.out.println(s.getSolution().toString());
        System.out.println("GAIN " + s.getSolution().gain());

    }

    public void testNoHurry() throws IOException, ParserException {
        File f = new File("input_files/c_no_hurry.in");
        Simulator s = new SimpleSimulator(new Parser(f).getRules(),DEFAULT);
        s.runSimulation();
        System.out.println(s.getSolution().toString());
        System.out.println("GAIN " + s.getSolution().gain());

    }

    public void testHighBonus() throws IOException, ParserException {
        File f = new File("input_files/e_high_bonus.in");
        Simulator s = new SimpleSimulator(new Parser(f).getRules(),DEFAULT);
        s.runSimulation();
        System.out.println(s.getSolution().toString());
        System.out.println("GAIN " + s.getSolution().gain());

    }

    @Test
    public void testExampleBestCase() throws IOException, ParserException {
        File f = new File("input_files/a_example.in");
        Rules rules = new Parser(f).getRules();

        int bestScore = 4;


        SimpleSimulator simu = new SimpleSimulator(rules, RidesOrderingStrategy.LATEST_START_FIRST_SHORT_PRIO);
        simu.allocateStrategyPool(5, new Position(rules.getNbColumns()/2, rules.getNbRows()/2), rules, 123456778912104L);
        simu.runSimulation();
        Solution sol = simu.getSolution();
        String better = sol.gain() > bestScore ? "WHOOHOOOOO !!! " : "Duh!";
        System.out.println(better + " - results " + sol.gain() );
    }

    @Test
    public void testMetropolisBestCase() throws IOException, ParserException {
        File f = new File("input_files/d_metropolis.in");
        Rules rules = new Parser(f).getRules();

        int bestScore = 11790470; // LATEST_START_FIRST_SHORT_PRIO  && simu.allocateIAmCloserStrategy(10000);

        //Rules newRules = rules.eliminateFarthestRides(.01).eliminateShortestRides(0.01);

        SimpleSimulator simu = new SimpleSimulator(rules, RidesOrderingStrategy.LATEST_START_FIRST_SHORT_PRIO);
        //simu.allocateStrategyPool(50, new Position(rules.getNbColumns()/2, rules.getNbRows()/2), rules, 123456778912104L);
        simu.allocateIAmClosestStrategy(10000);
        simu.runSimulation();
        Solution sol = simu.getSolution();
        String better = sol.gain() > bestScore ? "WHOOHOOOOO !!! " : "Duh!";
        System.out.println(better + " - results " + sol.gain() );
        writeSolutionToFile(sol, new File("test_metropolis.out"));

    }

    @Test
    public void testMetropolis() throws IOException, ParserException {
        File f = new File("input_files/d_metropolis.in");
        Rules rules = new Parser(f).getRules();

        int bestScore = 9495287;

        Rules newRules = rules.eliminateFarthestRides(.01);
        // Rules newRules = rules;

        long seeds[] = {
                1234567890L,
                1234567891L,
                12345678923L,
                123456789458L,
                11111111111L,
                22222222222L,
                333333333333L,
                4444444444444L};

        for(long seed : seeds){
            SimpleSimulator simu = new SimpleSimulator(newRules,RidesOrderingStrategy.LATEST_START_FIRST_SHORT_PRIO);
            simu.allocateRamdomizedStrategy(2,seed);
            simu.runSimulation();
            Solution sol = simu.getSolution();
            String better = sol.gain() > bestScore ? "WHOOHOOOOO !!! " : "Duh!";
            System.out.println(better + " - results " + sol.gain() + " seed: " + seed);
        }





    }



}