package sokrat.main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Parser {


    private Simulator simulator;

    public Parser(Reader dataIn) throws IOException, ParserExcception {
        try(BufferedReader reader = new BufferedReader(dataIn)){
            parseBaseInformation(reader.readLine());

            List<Ride> rides = new ArrayList<Ride>();
            String line= reader.readLine();;
            while ( line != null){
                rides.add(parseRide(line));
                line = reader.readLine();
            }
            getSimulator().addRides(rides);
        }

    }

    protected Ride parseRide(String line) throws ParserExcception {
        String[] fields = line.split("\\s+");
        if(fields.length < 6) {
            throw new ParserExcception("Invalid baseline data line : " + line);
        }
        return new Ride(
                new Position( Integer.valueOf(fields[0]),Integer.valueOf(fields[1])),
                new Position( Integer.valueOf(fields[2]),Integer.valueOf(fields[3])),
                Integer.valueOf(fields[4]),
                Integer.valueOf(fields[5])
        );
    }

    protected int parseBaseInformation(String s) throws ParserExcception{
        String[] fields = s.split("\\s+");
        if(fields.length < 6) {
            throw new ParserExcception("Invalid baseline data line : " + s);
        }
        setSimulator(new SimpleSimulator(
                Integer.valueOf(fields[5]),
                Integer.valueOf(fields[0]),
                Integer.valueOf(fields[1]),
                Integer.valueOf(fields[2]),
                Integer.valueOf(fields[4])));

        return Integer.valueOf(fields[3]);
    }

    public Parser(File file) throws IOException, ParserExcception {
        this(new FileReader( file ));

    }

    public Simulator getSimulator() {
        return simulator;
    }

    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }
}
