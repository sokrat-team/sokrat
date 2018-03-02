package sokrat.main;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    private List<Vehicle> vehicles;
    public int bonus = 20;
    int gain;

    public Solution(){
        vehicles = new ArrayList<>();
        gain = -1;
    }

    public Solution(List<Vehicle> vehicles, int bonus){
        this.vehicles = vehicles;
        for (Vehicle v : vehicles) {
            v.setRides(new ArrayList<>());
        }
        this.bonus = bonus;
        gain = -1;
    }

    public void addRideToVehicle(int vehindex, Ride ride){
        this.vehicles.get(vehindex).getRides().add(ride);
    }

    public int gain() {
        if(this.gain >=0){
            return this.gain;
        }
        gain = 0;
        for (Vehicle vehicle : this.vehicles) {
            int currentStep = 0;
            Position currentPos = new Position(0, 0);
            for (Ride ride : vehicle.getRides()) {
                currentStep += currentPos.distanceTo(ride.getFrom());
                if (currentStep <= ride.getEarliestStart()) {
                    gain += this.bonus;
                }
                currentPos = ride.getFrom();
                currentStep += currentPos.distanceTo(ride.getTo());
                int ridelength  = ride.getFrom().distanceTo(ride.getTo());
                if (currentStep <= ride.getLatestFinish()) {
                    gain += ridelength;
                }
                currentPos = ride.getTo();
                currentStep += ridelength;
            }
        }
        return gain;
    }
}
