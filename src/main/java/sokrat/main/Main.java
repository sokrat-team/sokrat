package sokrat.main;

import com.google.common.base.Preconditions;
import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    @Argument(alias = "i", description = "Input File, mandatory", required = true)
    private static File inputFile;

    @Argument(alias = "o", description = "Output File, optional", required = false)
    private static File outputFile = new File("output_file");

    final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {

        Args.parseOrExit(Main.class, args);

        try {
            new Main(inputFile,outputFile).proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }

    /**
     * @param inputFile
     * @param outputFile
     * @throws FileNotFoundException
     */
    public Main(File inputFile, File outputFile) throws FileNotFoundException {
        Preconditions.checkNotNull(inputFile,"input file must not be null");
        Preconditions.checkNotNull(outputFile, "output file must not be null");
        if(!inputFile.exists()) throw new FileNotFoundException();
        logger.info("Running with input file: {}",inputFile);
        logger.info("Will write to output file: {}",outputFile);
    }

    public void proceed() throws IOException, ParserExcception {
        Simulator s = new Parser(inputFile).getSimulator();
        GeneticAlgorithm g = new GeneticAlgorithm(s);
        Solution sol = g.solve();
        logger.info("Score genetic: {}", sol.gain());
        logger.info(sol.toString());
        s.runSimulation();
        logger.info("Score : " + s.getScore());
    }
}
