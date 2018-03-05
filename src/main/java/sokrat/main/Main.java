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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    private File inputFile;


    private File outputFile = new File("output_file");

    static final Logger logger = LoggerFactory.getLogger(Main.class);
    static String bestScoresStr ="";
    static int totalScore=0;


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

        List<Solution> results = new ArrayList<>();
        results.add(proceedGenetic(r));
        results.add(proceedShortestDistanceRides(r,"MOVE_TO_SHORTEST_EARLY", ShortestPathToRidesStrategy.DEFAULT_STRATEGY));
        //results.add(proceedShortestDistanceRides(r,"MOVE_TO_SHORTEST_AVG", ShortestPathToRidesStrategy.AVG_DISTANCE_STRATEGY));
        //results.add(proceedShortestDistanceRides(r,"MOVE_TO_SHORTEST_LAST_START", ShortestPathToRidesStrategy.LATEST_DISTANCE_STRATEGY));
        results.add(proceedNaive(r, RidesOrderingStrategy.EARLIEST_START_FIRST,"EARLIEST_START_FIRST"));
        results.add(proceedNaive(r, RidesOrderingStrategy.LATEST_START_LAST,"LATEST_START_LAST"));
        results.add(proceedNaive(r, RidesOrderingStrategy.LATEST_START_FIRST,"LATEST_START_FIRST"));
        results.add(proceedNaive(r, RidesOrderingStrategy.DEFAULT,"BY_INDEX"));
        results.add(proceedByVehicle(r,""));
        results.add(proceedNaive(r, new NearbyRideAffectationStrategy(r,20,RidesOrderingStrategy.EARLIEST_START_FIRST),"NEARBY_EARLIEST_START_FIRST"));
        results.add(proceedNaive(r, new NearbyRideAffectationStrategy(r,20,RidesOrderingStrategy.LATEST_START_LAST),"NEARBY_LATEST_START_LAST"));

        results.add(proceedGenetic(r, new ArrayList<Solution>(results)));

        Solution bestSol = results.stream().sorted((s1, s2) -> Integer.compare(s2.gain(), s1.gain())).findFirst().get();
        bestScoresStr = bestScoresStr +"\n"+inputFile.getName()+": "+NumberFormat.getIntegerInstance().format(bestSol.gain());
        totalScore+=bestSol.gain();
        writeSolutionToFile(bestSol, new File("best_results",outputFile.getName()));


    }

    private Solution proceedShortestDistanceRides(Rules r, String name, ShortestPathToRidesStrategy.NextRideFinder strategy) throws IOException {
        SimpleSimulator simu = new SimpleSimulator(r,RidesOrderingStrategy.LATEST_START_LAST);
        simu.setStrategy(new ShortestPathToRidesStrategy( ()->simu.getUnasssignedRides(), strategy ));
        Stopwatch timer = Stopwatch.createStarted();
        simu.runSimulation();
        Solution sol = simu.getSolution();

        sol.setGain(simu.calculateGain()); // because solution.gain() is bugged

        logger.info("Score naive ({}): {}", name, NumberFormat.getIntegerInstance().format(sol.gain()));

        logger.info("Time naive (ms): {}", NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
        writeSolutionToFile(sol, outputFile+".naive."+name);
        return sol;
    }

    private Solution proceedByVehicle(Rules r, String name) throws IOException {
        ByVehicleSimulator simu = new ByVehicleSimulator(r);

        Stopwatch timer = Stopwatch.createStarted();
        simu.runSimulation();
        Solution sol = simu.getSolution();

        sol.setGain(simu.calculateGain()); // because solution.gain() is bugged

        logger.info("Score BV ({}): {}", name, NumberFormat.getIntegerInstance().format(sol.gain()));

        logger.info("Time BV (ms): {}", NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
        writeSolutionToFile(sol, outputFile+".bv."+name);
        return sol;
    }

    private Solution proceedNaive(Rules r, AffectationStrategy strategy, String name) throws IOException {
        SimpleSimulator simu = new SimpleSimulator(r,RidesOrderingStrategy.DEFAULT);
        simu.setStrategy(strategy);
        Stopwatch timer = Stopwatch.createStarted();
        simu.runSimulation();
        Solution sol = simu.getSolution();

        sol.setGain(simu.calculateGain()); // because solution.gain() is bugged

        logger.info("Score naive ({}): {}", name, NumberFormat.getIntegerInstance().format(sol.gain()));

        logger.info("Time naive (ms): {}", NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
        writeSolutionToFile(sol, outputFile+".naive."+name);
        return sol;
    }

    private Solution proceedNaive(Rules r, RidesOrderingStrategy ordering,String name) throws IOException {
        SimpleSimulator simu = new SimpleSimulator(r,ordering);
        Stopwatch timer = Stopwatch.createStarted();
        simu.runSimulation();
        Solution sol = simu.getSolution();

        sol.setGain(simu.calculateGain()); // because solution.gain() is bugged

        logger.info("Score naive ({}): {}", name, NumberFormat.getIntegerInstance().format(sol.gain()));

        logger.info("Time naive (ms): {}", NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
        writeSolutionToFile(sol, outputFile+".naive."+name);
        return sol;
    }

    private Solution proceedGenetic(Rules rules) throws IOException {
        Stopwatch timer = Stopwatch.createStarted();
        GeneticAlgorithm g = createGenetic(rules);
        Solution sol = g.solveWithRandomInitialSet();
        logger.info("Score genetic: {}", NumberFormat.getIntegerInstance().format(sol.gain()));
        logger.info("Time genetic (ms): {}", NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
        writeSolutionToFile(sol,outputFile+".genetic");
        return sol;
    }

    private Solution proceedGenetic(Rules rules, List<Solution> solutions) throws IOException {
        Stopwatch timer = Stopwatch.createStarted();
        GeneticAlgorithm g = createGenetic(rules);
        Solution sol = g.solveWith(solutions);
        logger.info("Score genetic with best sol: {}", NumberFormat.getIntegerInstance().format(sol.gain()));
        logger.info("Time genetic with best sol (ms): {}", NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
        writeSolutionToFile(sol,outputFile+".genetic2");
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
}
