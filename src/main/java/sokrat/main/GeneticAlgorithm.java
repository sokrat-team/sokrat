package sokrat.main;

import java.util.*;

public class GeneticAlgorithm {

    public int bestGrade;

    public List<Solution> solutions;

    public Simulator simulator;

    public GeneticAlgorithm(Simulator simulator){
        this.bestGrade = 0;
        solutions = new ArrayList<>();
        this.simulator = simulator;
    }

    public Solution solve(){
        solutions = generateNSolutions(1000);
        for(int index = 0 ; index < 1 ; ++index){
            //gradeAll();
            solutions.sort(new Comparator<Solution>() {
                @Override
                public int compare(Solution o1, Solution o2) {
                    return o1.gain() - o2.gain();
                }
            });
            //removeNWeakest(980);
            //iterate();
        }
        if(solutions.size() > 0){
            return solutions.get(0);
        }
        return null;
    }

    public List<Solution> generateNSolutions(int poolsize){
        List<Solution> retVal = new ArrayList<>();
        for(int index = 0 ; index < poolsize-1 ; ++index){
            List<Vehicle> vehicles = new ArrayList<>();
            for(int vehindex = 0 ; vehindex < this.simulator.getNbVehicles(); ++vehindex){
                vehicles.add(new Vehicle(new Position(0,0)));
            }
            Solution sol = new Solution(vehicles, this.simulator.getBonus());
            assignRDMRides(this.simulator.getRides(), sol);
            retVal.add(sol);
            System.out.println("toto"+retVal.get(index).gain());
        }
        List<Vehicle> clevervehicles = new ArrayList<>();
        for(int vehindex = 0 ; vehindex < this.simulator.getNbVehicles(); ++vehindex){
            clevervehicles.add(new Vehicle(new Position(0,0)));
        }
        Solution cleversol = new Solution(clevervehicles, this.simulator.getBonus());
        assignCLEVERRides(this.simulator.getRides(), cleversol);
        retVal.add(cleversol);
        return retVal;
    }

    public void assignRDMRides(List<Ride> rides, Solution sol){
        for(Ride ride : rides){
            int vehicleIndex = (int)(Math.random() * this.simulator.getNbVehicles());
            sol.addRideToVehicle(vehicleIndex, ride);
        }
    }

    public void assignCLEVERRides(List<Ride> rides, Solution sol){
        for(int index = 0 ; index < rides.size() ; ++ index){
            int vehicleIndex = index % this.simulator.getNbVehicles();
            sol.addRideToVehicle(vehicleIndex, rides.get(index));
        }
    }

    public Solution getMutation(Solution parent){
        return null;
    }

    public Solution getCrossover(Solution mother, Solution motherfucker){
        return null;
    }

    public void iterate(){
        for(int index = 0 ; index < 750 ; index ++){
            int iParent = (int)(solutions.size()*Math.random());
            Solution parent = solutions.get(iParent);
            solutions.add(getMutation(parent));
        }
        for(int index = 0 ; index < 230; index ++){
            int iParent1 = (int)(solutions.size()*Math.random());
            int iParent2 = (int)(solutions.size()*Math.random());
            if(iParent1 == iParent2){
                iParent2++;
                if(iParent2 >= solutions.size()){
                    iParent2 = 0;
                }
            }
            Solution parent1 = solutions.get(iParent1);
            Solution parent2 = solutions.get(iParent2);
            solutions.add(getCrossover(parent1, parent2));
        }
    }


    public void gradeAll(){

    }

    public void removeNWeakest(int tailsize){
        List<Solution> remaining = new ArrayList<>();
        for(int index = 0 ; index < solutions.size() - tailsize; ++index){
            remaining.add(solutions.get(index));
        }
        solutions = remaining;
    }


}
