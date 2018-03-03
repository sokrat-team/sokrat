package sokrat.main.algorithms;

import sokrat.main.model.Position;
import sokrat.main.model.Ride;
import sokrat.main.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    private List<Vehicle> vehicles;
    public int bonus = 20;
    int gain=-1;

    public void setGain(int gain) {
        this.gain = gain;
    }

    public Solution(){
        vehicles = new ArrayList<>();
    }


    public Solution(List<Vehicle> vehicles, int bonus){
        this.vehicles = vehicles;
        for (Vehicle v : vehicles) {
            v.setRides(new ArrayList<>());
        }
        this.bonus = bonus;
    }

    public static Solution generateSolution(List<Vehicle> vehicles, int bonus){
        Solution results = new Solution();
        results.bonus = bonus;
        results.vehicles = vehicles;
        return results;
    }

    public void addRideToVehicle(int vehindex, Ride ride){
        this.vehicles.get(vehindex).getRides().add(ride);
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public int gain() {
        if(gain >= 0){
            return gain;
        }
        for (Vehicle vehicle : this.vehicles) {
            int currentStep = 0;
            Position currentPos = Position.INITIAL_POSITION;
            for (Ride ride : vehicle.getRides()) {

                currentStep += currentPos.distanceTo(ride.getFrom());
                if (currentStep < ride.getEarliestStart()) {
                    currentStep = ride.getEarliestStart();
                }
                ride.setActualStartTime(currentStep);
                currentPos = ride.getTo();
                currentStep += ride.getLength();
                ride.setActualArrivalTime(currentStep);
            }
        }
        gain = calculateGain();
        return gain;
    }

    public int calculateGain(){
        int gain = 0;
        for (Vehicle v : this.vehicles){
            for( Ride r : v.getRides()){
                if(r.finishedOnTime()){
                    gain+=r.getLength();
                    if(r.startedOnTime()) gain += bonus;
                }
            }
        }
        return gain;
    }


    public String toString(){
        String solutionsString = "";
        for(Vehicle vehicle : this.vehicles){
            solutionsString+=vehicle.getRides().size();
            for(Ride ride : vehicle.getRides()){
                solutionsString+=" "+ride.getIndex();
            }
            solutionsString+="\n";
        }
        return solutionsString;
    }
}
