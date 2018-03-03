package sokrat.main.definition;

import sokrat.main.model.Position;
import sokrat.main.model.Ride;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Parser {


    public static final int R = 0;
    public static final int C = 1;
    public static final int F = 2;
    public static final int N = 3;
    public static final int B = 4;
    public static final int T = 5;
    private Rules rules;
    private int currentIndex = 0;

    public Parser(Reader dataIn) throws IOException, ParserException {
        try(BufferedReader reader = new BufferedReader(dataIn)){
            parseBaseInformation(reader.readLine());

            List<Ride> rides = new ArrayList<Ride>();
            String line= reader.readLine();;
            while ( line != null){
                rides.add(parseRide(line));
                line = reader.readLine();
                currentIndex++;
            }
            rules.setRides(rides);
        }

    }

    protected Ride parseRide(String line) throws ParserException {
        String[] fields = line.split("\\s+");
        if(fields.length < 6) {
            throw new ParserException("Invalid baseline data line : " + line);
        }
        return new Ride(
                new Position( Integer.valueOf(fields[0]),Integer.valueOf(fields[1])),
                new Position( Integer.valueOf(fields[2]),Integer.valueOf(fields[3])),
                Integer.valueOf(fields[4]),
                Integer.valueOf(fields[5]),
                currentIndex
        );
    }

    protected int parseBaseInformation(String s) throws ParserException {
        String[] fields = s.split("\\s+");
        if(fields.length < 6) {
            throw new ParserException("Invalid baseline data line : " + s);
        }
        setRules(new Rules(
                Integer.valueOf(fields[R]),
                Integer.valueOf(fields[C]),
                Integer.valueOf(fields[T]),
                Integer.valueOf(fields[F]),
                Integer.valueOf(fields[B])));

        return Integer.valueOf(fields[N]);
    }

    public Parser(File file) throws IOException, ParserException {
        this(new FileReader( file ));
    }

    public Rules getRules() {
        return rules;
    }

    public void setRules(Rules rules) {
        this.rules = rules;
    }
}
