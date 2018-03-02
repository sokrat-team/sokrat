package sokrat.main;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.*;

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
    public void testSimple() throws IOException, ParserExcception {
        File f = new File("input_files/a_example.in");
        Simulator s = new Parser(f).getSimulator();
        s.runSimulation();
        System.out.println(s.getSolution().toString());
    }

    @Test
    public void testShouldBeEasy() throws IOException, ParserExcception {
        File f = new File("input_files/b_should_be_easy.in");
        Simulator s = new Parser(f).getSimulator();
        s.runSimulation();
        System.out.println(s.getSolution().toString());
        System.out.println("GAIN " + s.getSolution().gain());

    }

    @Test
    public void testNoHurry() throws IOException, ParserExcception {
        File f = new File("input_files/c_no_hurry.in");
        Simulator s = new Parser(f).getSimulator();
        s.runSimulation();
        System.out.println(s.getSolution().toString());
        System.out.println("GAIN " + s.getSolution().gain());

    }

    @Test
    public void testHighBonus() throws IOException, ParserExcception {
        File f = new File("input_files/e_high_bonus.in");
        Simulator s = new Parser(f).getSimulator();
        s.runSimulation();
        System.out.println(s.getSolution().toString());
        System.out.println("GAIN " + s.getSolution().gain());

    }

}