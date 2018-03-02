package sokrat.main;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    private static File inputFile;


    private static File outputFile = new File("output_file");

    final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {

        String[] files={"a_example","b_should_be_easy","c_no_hurry","d_metropolis","e_high_bonus"};

        for(String f : files) {
            try {

                new Main(new File("input_files",f+".in"), new File("output_files",f+".out").proceed();
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
    }

    public void proceed() throws IOException, ParserExcception {
        Stopwatch timer = Stopwatch.createStarted();
        Simulator s = new Parser(inputFile).getSimulator();
        logger.info("Score genetic: {}", sol.gain());
        s.runSimulation();
        logger.info("Score : " + s.getScore());
        logger.info("Time : " + timer.elapsed(TimeUnit.MILLISECONDS));
    }
}
