package sokrat.main;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sokrat.main.algorithms.Solution;
import sokrat.main.algorithms.genetic.GeneticAlgorithm;
import sokrat.main.algorithms.naive.SimpleSimulator;
import sokrat.main.definition.Parser;
import sokrat.main.algorithms.naive.Simulator;
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


    public static void main(String[] args) {

        String[] files={"a_example","b_should_be_easy","c_no_hurry","d_metropolis","e_high_bonus"};
        new File("output_files").mkdirs();


        for(String f : files) {
            try {
                logger.info("------------------   {}  --------------------- ", f);
                new Main(new File("input_files",f+".in"), new File("output_files",f+".out")).proceed();
            } catch (Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
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
        results.add(proceedNaive(new SimpleSimulator(r)));

        writeSolutionToFile(results.stream().sorted((s1,s2)->Integer.compare(s2.gain(),s1.gain())).findFirst().get(), outputFile);


    }

    private Solution proceedNaive(Simulator simu) throws IOException {
        Stopwatch timer = Stopwatch.createStarted();
        simu.runSimulation();
        Solution sol = simu.getSolution();
        logger.info("Score naive: {}", NumberFormat.getIntegerInstance().format(sol.gain()));

        sol.setGain(simu.calculateGain());
        logger.info("Score naive (2): {}", NumberFormat.getIntegerInstance().format(sol.gain()));

        logger.info("Time naive (ms): {}", NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
        writeSolutionToFile(sol, outputFile+".naive");
        return sol;
    }

    private Solution proceedGenetic(Rules rules) throws IOException {
        Stopwatch timer = Stopwatch.createStarted();
        GeneticAlgorithm g = new GeneticAlgorithm(rules);
        Solution sol = g.solve();
        logger.info("Score genetic: {}", NumberFormat.getIntegerInstance().format(sol.gain()));
        logger.info("Time genetic (ms): {}", NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
        writeSolutionToFile(sol,outputFile+".genetic");
        return sol;
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
