package sokrat.main;

import sokrat.main.algorithms.Solution;
import sokrat.main.definition.Rules;
import sokrat.main.model.Position;
import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SolutionLoader {

    public static Solution loadSolution(File outputFile, String name, Rules r) throws IOException {

        try( FileReader reader = new FileReader(outputFile)){
            return loadSolution(reader, name, r);
        }

    }

    public static Solution loadSolution(Reader r, String name, Rules rules) throws IOException {

        List<Vehicle> vehicules = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(r)){
            String line = reader.readLine();
            while( line != null){
                loadVehicle(line, rules).ifPresent(vehicules::add);
                line = reader.readLine();
            }

        }
        Solution s = Solution.generateSolution(vehicules, rules.getBonus());
        s.setName(name);
        return s;
    }

    private static Optional<Vehicle> loadVehicle(String line, Rules rules) {
        String[] rides = line.split("\\s");
        Vehicle results = new Vehicle(Position.INITIAL_POSITION);
        int  step = 0;
        for( String s : Arrays.copyOfRange(rides,1,rides.length)){

            if(!s.trim().equals("")) {
                Ride r = rules.getRides().get(Integer.parseInt(s));
                results.goForRide(r, step);
                step += results.getCurrentPosition().distanceTo(r.getFrom());
                step += r.getLength();
                results.endRide(step);
            }
        }
        return Optional.of(results);
    }
}
