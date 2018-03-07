package sokrat.main.model;

public class Ride {

    private int index;

    private Position from;
    private Position to;
    private int earliestStart;
    private int latestFinish;

    public Ride(Position from, Position to, int earliestStart, int latestFinish, int index) {
        this.from = from;
        this.to = to;
        this.earliestStart = earliestStart;
        this.latestFinish = latestFinish;
        this.setIndex(index);
    }

    private int actualStartTime;
    private int actualArrivalTime;


    public Position getFrom() {
        return from;
    }

    public void setFrom(Position from) {
        this.from = from;
    }

    public Position getTo() {
        return to;
    }

    public void setTo(Position to) {
        this.to = to;
    }

    public int getEarliestStart() {
        return earliestStart;
    }

    public void setEarliestStart(int earliestStart) {
        this.earliestStart = earliestStart;
    }

    public int getLatestFinish() {
        return latestFinish;
    }

    public void setLatestFinish(int latestFinish) {
        this.latestFinish = latestFinish;
    }

    public int getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(int actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public int getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(int actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLength() {
        return from.distanceTo(to);
    }

    public boolean startedOnTime() {
        return getActualStartTime() == getEarliestStart();
    }

    public boolean finishedOnTime() {
        return getActualArrivalTime() < getLatestFinish();
    }

    public int getLatestStart(){
        return latestFinish-getLength()-1;

    }

    public int distanceBetween(Ride that){
        int distance = to.distanceTo(that.getFrom());
        int maxArrivalStep = latestFinish+distance;
        if (that.getEarliestStart() > maxArrivalStep ) {
            distance += that.getEarliestStart() - maxArrivalStep;
        }
        return distance;

    }

    public int minimalLostTimeTo(Ride that){
        return that.getEarliestStart()-this.getLatestFinish();

    }


    public int timeLostToNextEarlyStart(Ride that, int stepBeforeStart){
        return Math.max(that.getEarliestStart(), stepBeforeStart+getLength()+distanceBetween(that))-this.getLatestFinish();
    }
    public int timeLostToNextLateStart(Ride that, int stepBeforeStart){
        return Math.max(that.getLatestStart(), stepBeforeStart+getLength()+distanceBetween(that))-this.getLatestFinish();
    }

    public int maximalLostTimeTo(Ride that){
        return that.getLatestStart()-this.getEarliestFinish();

    }

    public double avgLostTimeBetween(Ride that, int stepBeforeStart){
        return (timeLostToNextEarlyStart(that, stepBeforeStart)+timeLostToNextLateStart(that,stepBeforeStart))/2.0;
    }

    public int getEarliestFinish() {
        return earliestStart+getLength();
    }

    public boolean missed(int step){
        return step > getLatestStart();
    }


    public boolean canBeBefore(Ride r2) {
        return (getEarliestFinish() + distanceBetween(r2)) <= r2.getLatestStart();
    }
}
