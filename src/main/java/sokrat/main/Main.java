package sokrat.main;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sokrat.main.algorithms.Solution;
import sokrat.main.algorithms.genetic.GeneticAlgorithm;
import sokrat.main.algorithms.naive.*;
import sokrat.main.definition.Parser;
import sokrat.main.definition.ParserException;
import sokrat.main.definition.Rules;
import sokrat.main.model.Position;
import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static sokrat.main.SolutionLoader.loadSolution;

public class Main {

    public static final int NUMBER_OF_DISTRICTS = 20;
    private File inputFile;


    private File outputFile = new File("output_file");

    static final Logger logger = LoggerFactory.getLogger(Main.class);
    static String bestScoresStr ="";
    static int totalScore=0;
    static int totalMAxPoints=0;


    public static void main(String[] args) {

        //String[] files={"a_example","b_should_be_easy","c_no_hurry","d_metropolis","e_high_bonus"};
        String[] files={"d_metropolis"};
        new File("output_files").mkdirs();
        new File("best_results").mkdirs();

        Stopwatch timer = Stopwatch.createStarted();
        for(String f : files) {
            try {
                logger.info("------------------   {}  --------------------- ", f);
                new Main(new File("input_files",f+".in"), new File("output_files",f+".out")).proceed();
            } catch (Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        logger.info("------------------   FINISHED  --------------------- ");
        logger.info("------------------   Duration: {}  seconds",NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.SECONDS)));
        logger.info("BEST SCORES:" + bestScoresStr);
        logger.info("TOTAL SCORE:" + totalScore);
        logger.info("max possible score?:" + totalMAxPoints);
        System.exit(0);
    }

    /**
     * @param inputFile
     * @param outputFile
     * @throws FileNotFoundException
     */
    public Main(File inputFile, File outputFile) throws FileNotFoundException {
        Preconditions.checkNotNull(inputFile, "input file must not be null");
        Preconditions.checkNotNull(outputFile, "output file must not be null");
        if (!inputFile.exists()) throw new FileNotFoundException();
        logger.info("Running with input file: {}", inputFile);
        logger.info("Will write to output file: {}", outputFile);
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void proceed() throws IOException, ParserException {
        Rules rules = new Parser(inputFile).getRules();
        logger.info("Doing " );

        List<Solution> results = new ArrayList<>();

        //results.add(calculateSolutionIfNeeded("genetic",rules,(r)->proceedGenetic(r,"genetic")));
        results.add(calculateSolutionIfNeeded("MOVE_TO_SHORTEST_EARLY",rules,(r)->proceedShortestDistanceRides(rules,"MOVE_TO_SHORTEST_EARLY", ShortestPathToRidesStrategy.DEFAULT_STRATEGY)));
        results.add(calculateSolutionIfNeeded("MOVE_TO_SHORTEST_AVG",rules,(r)->proceedShortestDistanceRides(rules,"MOVE_TO_SHORTEST_AVG", ShortestPathToRidesStrategy.AVG_DISTANCE_STRATEGY)));
        results.add(calculateSolutionIfNeeded("MOVE_TO_SHORTEST_LATESTART",rules,(r)->proceedShortestDistanceRides(rules,"MOVE_TO_SHORTEST_LATESTART", ShortestPathToRidesStrategy.LATEST_DISTANCE_STRATEGY)));
        results.add(calculateSolutionIfNeeded("NAIVE_EARLIEST_START_FIRST",rules,(r)->proceedNaive(rules, RidesOrderingStrategy.EARLIEST_START_FIRST,"EARLIEST_START_FIRST")));
        results.add(calculateSolutionIfNeeded("NAIVE_LATEST_START_LAST",rules,(r)->proceedNaive(rules, RidesOrderingStrategy.LATEST_START_LAST,"LATEST_START_LAST")));
        results.add(calculateSolutionIfNeeded("NAIVE_LATEST_START_FIRST",rules,(r)->proceedNaive(rules, RidesOrderingStrategy.LATEST_START_FIRST,"LATEST_START_FIRST")));
//        results.add(calculateSolutionIfNeeded("BY_INDEX",rules,(r)->proceedNaive(rules, RidesOrderingStrategy.DEFAULT,"BY_INDEX")));
//        results.add(calculateSolutionIfNeeded("BY_VEHICULE_BASIC",rules,(r)->proceedByVehicle(rules,"BY_VEHICULE_BASIC")));
        results.add(calculateSolutionIfNeeded("NEARBY_EARLIEST_START_FIRST",rules,(r)->proceedNaive(rules,new NearbyRideAffectationStrategy(rules, NUMBER_OF_DISTRICTS,RidesOrderingStrategy.EARLIEST_START_FIRST),"NEARBY_EARLIEST_START_FIRST")));
        results.add(calculateSolutionIfNeeded("NEARBY_DEFAULT",rules,(r)->proceedNaive(rules,new NearbyRideAffectationStrategy(rules,20,RidesOrderingStrategy.DEFAULT),"NEARBY_DEFAULT")));
        results.add(calculateSolutionIfNeeded("NEARBY_LATEST_START_FIRST",rules,(r)->proceedNaive(rules,new NearbyRideAffectationStrategy(rules,20,RidesOrderingStrategy.LATEST_START_FIRST),"NEARBY_LATEST_START_FIRST")));
        results.add(calculateSolutionIfNeeded("NEARBY_LATEST_START_LAST",rules,(r)->proceedNaive(rules, new NearbyRideAffectationStrategy(rules,20,RidesOrderingStrategy.LATEST_START_LAST),"NEARBY_LATEST_START_LAST")));

        Rules newRules = rules.eliminateShortestRides(0.05).eliminateFarthestRides(.05);
        results.add(calculateSolutionIfNeeded("FILTERED_MOVE_TO_SHORTEST_EARLY",newRules,(r)->proceedShortestDistanceRides(r,"FILTERED_MOVE_TO_SHORTEST_EARLY", ShortestPathToRidesStrategy.DEFAULT_STRATEGY)));
        results.add(calculateSolutionIfNeeded("FILTERED_MOVE_TO_SHORTEST_LATESTART",newRules,(r)->proceedShortestDistanceRides(r,"FILTERED_MOVE_TO_SHORTEST_LATESTART", ShortestPathToRidesStrategy.LATEST_DISTANCE_STRATEGY)));
        results.add(calculateSolutionIfNeeded("FILTERED_MOVE_TO_SHORTEST_AVG",newRules,(r)->proceedShortestDistanceRides(r,"FILTERED_MOVE_TO_SHORTEST_AVG", ShortestPathToRidesStrategy.AVG_DISTANCE_STRATEGY)));
        results.add(calculateSolutionIfNeeded("FILTERED_NAIVE_EARLIEST_START_FIRST",newRules,(r)->proceedNaive(r, RidesOrderingStrategy.EARLIEST_START_FIRST,"FILTERED_EARLIEST_START_FIRST")));
        results.add(calculateSolutionIfNeeded("FILTERED_NAIVE_NAIVE_LATEST_START_FIRST",newRules,(r)->proceedNaive(r, RidesOrderingStrategy.LATEST_START_FIRST,"FILTERED_NAIVE_NAIVE_LATEST_START_FIRST")));
        results.add(calculateSolutionIfNeeded("FILTERED_NAIVE_NAIVE_LATEST_START_LAST",newRules,(r)->proceedNaive(r, RidesOrderingStrategy.LATEST_START_LAST,"FILTERED_NAIVE_NAIVE_LATEST_START_LAST")));

        Solution bestSol = results.stream().sorted((s1, s2) -> Integer.compare(s2.gain(), s1.gain())).findFirst().get();
        bestScoresStr = bestScoresStr +"\n"+inputFile.getName()+": " + bestSol.getName() + " with "+NumberFormat.getIntegerInstance().format(bestSol.gain()) + " (max " + NumberFormat.getIntegerInstance().format(rules.getMaxPoints())+")";
        totalScore+=bestSol.gain();
        totalMAxPoints+=rules.getMaxPoints();
        writeSolutionToFile(bestSol, new File("best_results",outputFile.getName()));


    }

    private Solution proceedShortestDistanceRides(Rules r, String name, ShortestPathToRidesStrategy.NextRideFinder strategy) {
        SimpleSimulator simu = new SimpleSimulator(r,RidesOrderingStrategy.LATEST_START_LAST);
        simu.setStrategy(new ShortestPathToRidesStrategy( ()->simu.getUnasssignedRides(), strategy ));

        return doSimulation(name, simu);
    }

    private Solution doSimulation(String name, Simulator simu){
        simu.runSimulation();
        Solution sol = simu.getSolution();
        sol.setName(name);

        sol.setGain(simu.calculateGain()); // because solution.gain() is bugged

        return sol;
    }

    private void recordResults(String name, Stopwatch timer, Solution sol)  {
        logger.info("Score {}: {}", name, NumberFormat.getIntegerInstance().format(sol.gain()));

        logger.info("Time {} (ms): {}", name, NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
        try {
            writeSolutionToFile(sol, outputFileName(name));
        } catch (IOException e) {
            logger.error("Cannot write solution to file", e);
        }
    }

    private String outputFileName(String name) {
        return outputFile+"."+name;
    }

    private Solution proceedByVehicle(Rules r, String name) {
        ByVehicleSimulator simu = new ByVehicleSimulator(r);

        return doSimulation(name, simu);
    }

    private Solution proceedNaive(Rules r, AffectationStrategy strategy, String name)  {
        SimpleSimulator simu = new SimpleSimulator(r,RidesOrderingStrategy.DEFAULT);
        simu.setStrategy(strategy);

        return doSimulation(name, simu);
    }

    private Solution proceedNaive(Rules r, RidesOrderingStrategy ordering,String name) {
        SimpleSimulator simu = new SimpleSimulator(r,ordering);

        return doSimulation(name, simu);
    }

    private Solution proceedGenetic(Rules rules, String name)  {
        GeneticAlgorithm g = createGenetic(rules);
        Solution sol = g.solveWithRandomInitialSet();
        return sol;
    }

    private Solution proceedGenetic(Rules rules, List<Solution> solutions, String name)  {
        GeneticAlgorithm g = createGenetic(rules);
        Solution sol = g.solveWith(solutions);
        return sol;
    }

    private GeneticAlgorithm createGenetic(Rules rules) {
        return new GeneticAlgorithm(rules,100, 90);
    }

    private void writeSolutionToFile(Solution sol, String file) throws IOException {
        writeSolutionToFile(sol,new File(file));
    }
    private void writeSolutionToFile(Solution sol, File file) throws IOException {
        FileWriter w = new FileWriter(file);
        w.write(sol.toString());
        w.close();
    }

    public Solution calculateSolutionIfNeeded(String name, Rules r, SolutionFinder f) {
        Stopwatch timer = Stopwatch.createStarted();
        Solution sol = calculateSolutionIfNeeded(name, r, f, timer);
        recordResults(name, timer, sol);
        return sol;
    }

    public Solution calculateSolutionIfNeeded(String name, Rules r, SolutionFinder f, Stopwatch timer) {
        File solFile = new File(outputFileName(name));
        try{
            if(solFile.exists() ){
                logger.info("Do not need to compute {}, loading from file: {}",name, solFile);
                return loadSolution(solFile, name, r);
            }
        }catch (Exception e){
            logger.error("IOException ", e);
        }
        return f.proceeed(r);
    }



    public abstract interface SolutionFinder{
        public Solution proceeed(Rules r);
    }
}
