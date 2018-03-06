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

public class Main {

    private File inputFile;


    private File outputFile = new File("output_file");

    static final Logger logger = LoggerFactory.getLogger(Main.class);
    static String bestScoresStr ="";
    static int totalScore=0;
    static int totalMAxPoints=0;


    public static void main(String[] args) {

        String[] files={"a_example","b_should_be_easy","c_no_hurry","d_metropolis","e_high_bonus"};
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
        Rules r = new Parser(inputFile).getRules();
        logger.info("Doing " );

        List<Solution> results = new ArrayList<>();
        results.add(proceedGenetic(r,"genetic"));
        results.add(proceedShortestDistanceRides(r,"MOVE_TO_SHORTEST_EARLY", ShortestPathToRidesStrategy.DEFAULT_STRATEGY));
        results.add(proceedShortestDistanceRides(r,"MOVE_TO_SHORTEST_AVG", ShortestPathToRidesStrategy.AVG_DISTANCE_STRATEGY));
        results.add(proceedShortestDistanceRides(r,"MOVE_TO_SHORTEST_LATESTART", ShortestPathToRidesStrategy.LATEST_DISTANCE_STRATEGY));
        results.add(proceedNaive(r, RidesOrderingStrategy.EARLIEST_START_FIRST,"EARLIEST_START_FIRST"));
        results.add(proceedNaive(r, RidesOrderingStrategy.LATEST_START_LAST,"LATEST_START_LAST"));
        results.add(proceedNaive(r, RidesOrderingStrategy.LATEST_START_FIRST,"LATEST_START_FIRST"));
        results.add(proceedNaive(r, RidesOrderingStrategy.DEFAULT,"BY_INDEX"));
        results.add(proceedByVehicle(r,"BV"));
        results.add(proceedNaive(r, new NearbyRideAffectationStrategy(r,20,RidesOrderingStrategy.EARLIEST_START_FIRST),"NEARBY_EARLIEST_START_FIRST"));
        results.add(proceedNaive(r, new NearbyRideAffectationStrategy(r,20,RidesOrderingStrategy.LATEST_START_LAST),"NEARBY_LATEST_START_LAST"));

        results.add(proceedGenetic(r, new ArrayList<Solution>(results),"genetic2"));

        Solution bestSol = results.stream().sorted((s1, s2) -> Integer.compare(s2.gain(), s1.gain())).findFirst().get();
        bestScoresStr = bestScoresStr +"\n"+inputFile.getName()+": "+NumberFormat.getIntegerInstance().format(bestSol.gain()) + " (max " + NumberFormat.getIntegerInstance().format(r.getMaxPoints())+")";
        totalScore+=bestSol.gain();
        totalMAxPoints+=r.getMaxPoints();
        writeSolutionToFile(bestSol, new File("best_results",outputFile.getName()));


    }

    private Solution proceedShortestDistanceRides(Rules r, String name, ShortestPathToRidesStrategy.NextRideFinder strategy) throws IOException {
        SimpleSimulator simu = new SimpleSimulator(r,RidesOrderingStrategy.LATEST_START_LAST);
        simu.setStrategy(new ShortestPathToRidesStrategy( ()->simu.getUnasssignedRides(), strategy ));

        return doSimulation(name, simu);
    }

    private Solution doSimulation(String name, Simulator simu) throws IOException {
        Stopwatch timer = Stopwatch.createStarted();
        simu.runSimulation();
        Solution sol = simu.getSolution();
        sol.setName(name);

        sol.setGain(simu.calculateGain()); // because solution.gain() is bugged

        recordResults(name, timer, sol);
        return sol;
    }

    private void recordResults(String name, Stopwatch timer, Solution sol) throws IOException {
        logger.info("Score {}: {}", name, NumberFormat.getIntegerInstance().format(sol.gain()));

        logger.info("Time {} (ms): {}", name, NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
        writeSolutionToFile(sol, outputFileName(name));
    }

    private String outputFileName(String name) {
        return outputFile+"."+name;
    }

    private Solution proceedByVehicle(Rules r, String name) throws IOException {
        ByVehicleSimulator simu = new ByVehicleSimulator(r);

        return doSimulation(name, simu);
    }

    private Solution proceedNaive(Rules r, AffectationStrategy strategy, String name) throws IOException {
        SimpleSimulator simu = new SimpleSimulator(r,RidesOrderingStrategy.DEFAULT);
        simu.setStrategy(strategy);

        return doSimulation(name, simu);
    }

    private Solution proceedNaive(Rules r, RidesOrderingStrategy ordering,String name) throws IOException {
        SimpleSimulator simu = new SimpleSimulator(r,ordering);

        return doSimulation(name, simu);
    }

    private Solution proceedGenetic(Rules rules, String name) throws IOException {
        Stopwatch timer = Stopwatch.createStarted();
        GeneticAlgorithm g = createGenetic(rules);
        Solution sol = g.solveWithRandomInitialSet();
        recordResults(name, timer, sol);
        return sol;
    }

    private Solution proceedGenetic(Rules rules, List<Solution> solutions, String name) throws IOException {
        Stopwatch timer = Stopwatch.createStarted();
        GeneticAlgorithm g = createGenetic(rules);
        Solution sol = g.solveWith(solutions);
        recordResults(name, timer, sol);
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

    public Solution calculateSolutionIfNeeded(String name, SolutionFinder f, Rules r) throws IOException {
        File solFile = new File(outputFileName(name));
        if(solFile.exists() ){
            return loadSolution(outputFile, name, r);
        }else{
            return f.proceeed(r);
        }
    }

    private Solution loadSolution(File outputFile, String name, Rules r) throws IOException {
        List<Vehicle> vehicules = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(outputFile))){
            String line = reader.readLine();
            while( line != null){
                loadVehicle(line, r).ifPresent(vehicules::add);
            }

        }
        Solution s = Solution.generateSolution(vehicules, r.getBonus());
        s.setName(name);
        return s;
    }

    private Optional<Vehicle> loadVehicle(String line, Rules rules) {
        String[] rides = line.split("\\s");
        Vehicle results = new Vehicle(Position.INITIAL_POSITION);
        int  step = 0;
        for( String s : rides){
            Ride r = rules.getRides().get(Integer.parseInt(s));
            results.goForRide(r, step);
            step+=results.getCurrentPosition().distanceTo(r.getFrom());
            step+=r.getLength();
            results.endRide(step);
        }
        return Optional.of(results);
    }

    public abstract interface SolutionFinder{
        public Solution proceeed(Rules r);
    }
}
