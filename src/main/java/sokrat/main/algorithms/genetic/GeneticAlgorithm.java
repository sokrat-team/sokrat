package sokrat.main.algorithms.genetic;

import sokrat.main.algorithms.Solution;
import sokrat.main.definition.Rules;
import sokrat.main.model.Position;
import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.util.*;

public class GeneticAlgorithm {

    public int bestGrade;

    public List<Solution> solutions;

    public Rules rules;
    int poolsize;
    int tail;
    final long SEED = 1234567890L;
    Random random = new Random(SEED);

    public GeneticAlgorithm(Rules rules, int poolsize, int tailSize){
        this.bestGrade = 0;
        this.poolsize=poolsize;
        this.tail=tailSize;
        solutions = new ArrayList<>();
        this.rules = rules;
    }

    public Solution solveWithRandomInitialSet(){
        solutions = generateNSolutions(poolsize);

        return solve();
    }

    private Solution solve() {
        for(int index = 0 ; index < 2 ; ++index){
            solutions.sort(new Comparator<Solution>() {
                @Override
                public int compare(Solution o1, Solution o2) {
                    return o2.gain() - o1.gain();
                }
            });
            removeNWeakest(tail);
            iterate();
        }
        if(solutions.size() > 0){
            return solutions.get(0);
        }
        return null;
    }

    public Solution solveWith( List<Solution> initialSolutions){
        solutions = new ArrayList<>();
        solutions.addAll(0,initialSolutions);
        solutions.addAll(generateNSolutions(poolsize-initialSolutions.size()));

        return solve();
    }

    public List<Solution> generateNSolutions(int poolsize){
        List<Solution> retVal = new ArrayList<>();
        for(int index = 0 ; index < poolsize-1 ; ++index){
            List<Vehicle> vehicles = new ArrayList<>();
            for(int vehindex = 0; vehindex < this.rules.getNbVehicles(); ++vehindex){
                vehicles.add(new Vehicle(new Position(0,0)));
            }
            Solution sol = new Solution(vehicles, this.rules.getBonus());
            assignFEASIBLERides(this.rules.getRides(), sol);
            retVal.add(sol);
        }
        List<Vehicle> clevervehicles = new ArrayList<>();
        for(int vehindex = 0; vehindex < this.rules.getNbVehicles(); ++vehindex){
            clevervehicles.add(new Vehicle(new Position(0,0)));
        }
        Solution cleversol = new Solution(clevervehicles, this.rules.getBonus());
        assignCLEVERRides(this.rules.getRides(), cleversol);
        retVal.add(cleversol);
        return retVal;
    }

    public void assignRDMRides(List<Ride> rides, Solution sol){
        for(Ride ride : rides){
            int vehicleIndex = random.nextInt(this.rules.getNbVehicles());
            sol.addRideToVehicle(vehicleIndex, ride);
        }
    }

    public void assignCLEVERRides(List<Ride> rides, Solution sol){
        for(int index = 0 ; index < rides.size() ; ++ index){
            int vehicleIndex = index % this.rules.getNbVehicles();
            sol.addRideToVehicle(vehicleIndex, rides.get(index));
        }
    }

    public void assignFEASIBLERides(List<Ride> rides, Solution sol){
        for(int index = 0 ; index < rides.size() ; ++ index){
            Ride ride = rides.get(index);
            int rdmStartIndex = random.nextInt(this.rules.getNbVehicles());;
            for(int vehindex = rdmStartIndex; vehindex < sol.getVehicles().size()+rdmStartIndex; ++vehindex){
                Vehicle car = sol.getVehicles().get(vehindex%sol.getVehicles().size());
                if(isFeasible(car, ride)){
                    sol.addRideToVehicle(vehindex%sol.getVehicles().size(), rides.get(index));
                    break;
                }

            }
        }
    }


    public boolean isFeasible(Vehicle car, Ride ride){
        return car.getRides().size() == 0
                ||      car.getTimeAtTopOfRideStack() +
                car.getRides().get(car.getRides().size()-1).getTo().distanceTo(ride.getFrom()) +
                ride.getFrom().distanceTo(ride.getTo())
                < ride.getLatestFinish();
    }

    public Solution getMutation(Solution parent){
        List<Vehicle> vehicles = new ArrayList<>();
        for(int vehindex = 0; vehindex < this.rules.getNbVehicles(); ++vehindex){
            vehicles.add(new Vehicle(new Position(0,0)));
        }
        Solution child = new Solution(vehicles, this.rules.getBonus());
        int permutationIndex = random.nextInt(rules.getRides().size());
        int count = 0;
        int vehicleCount = 0;
        for(Vehicle vhparent : parent.getVehicles()){
            for(Ride ride : vhparent.getRides()){
                count++;
                if(count == permutationIndex){
                    int randomette = random.nextInt(parent.getVehicles().size());
                    if(randomette == vehicleCount){
                        randomette++;
                        if(randomette >=parent.getVehicles().size()){
                            randomette = 0;
                        }
                    }
                    child.addRideToVehicle(randomette, ride);
                }
                else
                {
                    child.addRideToVehicle(vehicleCount, ride);
                }
            }
            vehicleCount++;
        }
        return child;
    }

    public Solution getCrossover(Solution mother, Solution motherfucker){
        return null;
    }

    public void iterate(){
        for(int index = 0 ; index < tail ; index ++){
            int iParent = random.nextInt(solutions.size());
            Solution parent = solutions.get(iParent);
            Solution child = getMutation(parent);
            solutions.add(child);
        }
    }



    public void removeNWeakest(int tailsize){
        solutions = new ArrayList(solutions.subList(0,poolsize-tail));
    }


}
