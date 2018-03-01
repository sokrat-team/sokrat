package sokrat.main;

public class Ride {

    private Position from;
    private Position to;
    private int earliestStart;
    private int latestFinish;

    public Ride(Position from, Position to, int earliestStart, int latestFinish) {
        this.from = from;
        this.to = to;
        this.earliestStart = earliestStart;
        this.latestFinish = latestFinish;
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
}
