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

}