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
    int poolsize = 100;
    int tail = 90;

    public GeneticAlgorithm(Rules rules){
        this.bestGrade = 0;
        solutions = new ArrayList<>();
        this.rules = rules;
    }

    public Solution solve(){
        solutions = generateNSolutions(poolsize);
        //int indexBLAH = 0;
        //for(Solution sol : solutions){
        //    System.out.println("solution "+indexBLAH+" gains: "+sol.gain()+" _______________");
        //    ++indexBLAH;
        //    System.out.println(sol);
        //}
        for(int index = 0 ; index < 2 ; ++index){
            //gradeAll();
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

    public List<Solution> generateNSolutions(int poolsize){
        List<Solution> retVal = new ArrayList<>();
        for(int index = 0 ; index < poolsize-1 ; ++index){
            List<Vehicle> vehicles = new ArrayList<>();
            for(int vehindex = 0; vehindex < this.rules.getNbVehicles(); ++vehindex){
                vehicles.add(new Vehicle(new Position(0,0)));
            }
            Solution sol = new Solution(vehicles, this.rules.getBonus());
            assignFEASIBLERides(this.rules.getRides(), sol);
            //System.out.println(sol);
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
            int vehicleIndex = (int)(Math.random() * this.rules.getNbVehicles());
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
            int rdmStartIndex = (int)(Math.random()*sol.getVehicles().size());
            for(int vehindex = rdmStartIndex; vehindex < sol.getVehicles().size()+rdmStartIndex; ++vehindex){
                Vehicle car = sol.getVehicles().get(vehindex%sol.getVehicles().size());
                if(isFeasible(car, ride)){
                    sol.addRideToVehicle(vehindex%sol.getVehicles().size(), rides.get(index));
                    break;
                }

            }
        }
    }

    //public void assignFeasibleToNearest(List<Ride> rides, Solution sol){
    //    for(int index = 0 ; index < rides.size() ; ++ index){
    //        Ride ride = rides.get(index);
    //        int rdmStartIndex = (int)(Math.random()*sol.getVehicles().size());
    //        int leastDist = Integer.MAX_VALUE;
    //        for(int vehindex = rdmStartIndex; vehindex < sol.getVehicles().size()+rdmStartIndex; ++vehindex){
    //            Vehicle car = sol.getVehicles().get(vehindex%sol.getVehicles().size());
//
    //            if(isFeasible(car, ride) && car.get){
    //                leastDist =
    //                //sol.addRideToVehicle(vehindex%sol.getVehicles().size(), rides.get(index));
    //            }
//
    //        }
    //    }
    //}

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
        int permutationIndex = (int)(this.rules.getRides().size() * Math.random());
        int count = 0;
        int vehicleCount = 0;
        for(Vehicle vhparent : parent.getVehicles()){
            for(Ride ride : vhparent.getRides()){
                count++;
                if(count == permutationIndex){
                    int randomette = (int)(Math.random() * parent.getVehicles().size());
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
            int iParent = (int)(solutions.size()*Math.random());
            Solution parent = solutions.get(iParent);
            Solution child = getMutation(parent);
            solutions.add(child);
        }
        //for(int index = 0 ; index < 230; index ++){
        //    int iParent1 = (int)(solutions.size()*Math.random());
        //    int iParent2 = (int)(solutions.size()*Math.random());
        //    if(iParent1 == iParent2){
        //        iParent2++;
        //        if(iParent2 >= solutions.size()){
        //            iParent2 = 0;
        //        }
        //    }
        //    Solution parent1 = solutions.get(iParent1);
        //    Solution parent2 = solutions.get(iParent2);
        //    solutions.add(getCrossover(parent1, parent2));
        //}
    }


    public void gradeAll(){

    }

    public void removeNWeakest(int tailsize){
        List<Solution> remaining = new ArrayList<>();
        for(int index = 0 ; index < solutions.size() - tailsize; ++index){
            remaining.add(solutions.get(index));
        }
        solutions = new ArrayList(remaining);
    }


}