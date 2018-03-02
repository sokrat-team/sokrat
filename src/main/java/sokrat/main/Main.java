package sokrat.main;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class Main {

    private File inputFile;


    private File outputFile = new File("output_file");

    final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {

        String[] files={"a_example","b_should_be_easy","c_no_hurry","d_metropolis","e_high_bonus"};

        for(String f : files) {
            try {

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
        outputFile.mkdirs();
        Preconditions.checkNotNull(inputFile, "input file must not be null");
        Preconditions.checkNotNull(outputFile, "output file must not be null");
        if (!inputFile.exists()) throw new FileNotFoundException();
        logger.info("Running with input file: {}", inputFile);
        logger.info("Will write to output file: {}", outputFile);
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void proceed() throws IOException, ParserExcception {
        Simulator s = new Parser(inputFile).getSimulator();
        proceedGenetic( s);
        proceedNaive( s);
    }

    private void proceedNaive(Simulator s) throws IOException {
        Stopwatch timer = Stopwatch.createStarted();
        s.runSimulation();
        Solution sol = s.getSolution();
        logger.info("Score naive: {}", NumberFormat.getIntegerInstance().format(sol.gain()));
        logger.info("Time naive (ms): {}", NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
        FileWriter w = new FileWriter(outputFile+".naive");
        w.write(s.getSolution().toString());
        w.close();
    }

    private void proceedGenetic(Simulator s) throws IOException {
        Stopwatch timer = Stopwatch.createStarted();
        GeneticAlgorithm g = new GeneticAlgorithm(s);
        Solution sol = g.solve();
        logger.info("Score genetic: {}", NumberFormat.getIntegerInstance().format(sol.gain()));
        logger.info("Time genetic (ms): {}", NumberFormat.getIntegerInstance().format(timer.elapsed(TimeUnit.MILLISECONDS)));
        FileWriter w = new FileWriter(outputFile+".genetic");
        w.write(sol.toString());
        w.close();
    }
}
